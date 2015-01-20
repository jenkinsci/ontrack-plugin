package net.nemerosa.ontrack.jenkins;

import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Notifier;
import hudson.tasks.Publisher;
import net.nemerosa.ontrack.dsl.http.OTHttpClientException;
import net.nemerosa.ontrack.jenkins.dsl.OntrackDSL;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.IOException;

/**
 * Notifier that allows to call the Ontrack DSL.
 */
public class OntrackDSLNotifier extends Notifier {

    private final boolean usingText;
    private final String scriptPath;
    private final String scriptText;
    private final String injectEnvironment;
    private final String injectProperties;
    private final boolean ontrackLog;

    @DataBoundConstructor
    public OntrackDSLNotifier(ScriptLocation scriptLocation, String injectEnvironment, String injectProperties, boolean ontrackLog) {
        this.usingText = scriptLocation == null || scriptLocation.isUsingText();
        this.scriptPath = scriptLocation == null ? null : scriptLocation.getScriptPath();
        this.scriptText = scriptLocation == null ? null : scriptLocation.getScriptText();
        this.injectEnvironment = injectEnvironment;
        this.injectProperties = injectProperties;
        this.ontrackLog = ontrackLog;
    }

    public boolean isUsingText() {
        return usingText;
    }

    public String getScriptPath() {
        return scriptPath;
    }

    public String getScriptText() {
        return scriptText;
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
        // Reads the script text
        String script;
        if (usingText) {
            script = scriptText;
        } else {
            FilePath path = theBuild.getWorkspace().child(scriptPath);
            script = path.readToString();
        }
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
        } catch (OTHttpClientException ex) {
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
