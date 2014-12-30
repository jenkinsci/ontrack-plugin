package net.nemerosa.ontrack.jenkins;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Notifier;
import hudson.tasks.Publisher;
import net.nemerosa.ontrack.client.ClientException;
import net.nemerosa.ontrack.jenkins.dsl.OntrackDSL;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.IOException;

/**
 * Notifier that allows to call the Ontrack DSL.
 */
public class OntrackDSLNotifier extends Notifier {

    private final String script;
    private final String injectEnvironment;
    private final String injectProperties;
    private final boolean ontrackLog;

    @DataBoundConstructor
    public OntrackDSLNotifier(String script, String injectEnvironment, String injectProperties, boolean ontrackLog) {
        this.script = script;
        this.injectEnvironment = injectEnvironment;
        this.injectProperties = injectProperties;
        this.ontrackLog = ontrackLog;
    }

    public String getScript() {
        return script;
    }

    public String getInjectEnvironment() {
        return injectEnvironment;
    }

    public String getInjectProperties() {
        return injectProperties;
    }

    public boolean isOntrackLog() {
        return ontrackLog;
    }

    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.BUILD;
    }

    @Override
    public boolean perform(AbstractBuild<?, ?> theBuild, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {
        // Ontrack DSL support
        OntrackDSL dsl = new OntrackDSL(
                script,
                injectEnvironment,
                injectProperties,
                ontrackLog
        );
        // Runs the script
        try {
            dsl.run(theBuild, listener);
        } catch (ClientException ex) {
            listener.getLogger().format("Ontrack DSL script failed with:%n%s%n", ex.getMessage());
            if (ontrackLog) {
                ex.printStackTrace(listener.getLogger());
            }
            theBuild.setResult(Result.FAILURE);
        }
        // End
        return true;
    }

    @Extension
    public static final class OntrackDSLNotifierDescriptorImpl extends BuildStepDescriptor<Publisher> {

        public OntrackDSLNotifierDescriptorImpl() {
            super(OntrackDSLNotifier.class);
        }

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return "Ontrack: DSL action";
        }
    }
}
