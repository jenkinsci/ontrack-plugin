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
import net.nemerosa.ontrack.dsl.http.OTHttpClientException;
import net.nemerosa.ontrack.dsl.http.OTMessageClientException;
import net.nemerosa.ontrack.jenkins.dsl.JenkinsConnector;
import net.nemerosa.ontrack.jenkins.dsl.OntrackDSLRunner;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.IOException;

/**
 * Notifier that allows to call the Ontrack DSL.
 */
public class OntrackDSLNotifier extends Notifier {

    private final boolean usingText;
    private final String scriptPath;
    private final String scriptText;
    private final boolean sandbox;
    private final String injectEnvironment;
    private final String injectProperties;
    private final boolean ontrackLog;

    @DataBoundConstructor
    public OntrackDSLNotifier(ScriptLocation ontrackScriptLocation, boolean sandbox, String injectEnvironment, String injectProperties, boolean ontrackLog) {
        this.usingText = ontrackScriptLocation == null || ontrackScriptLocation.isUsingText();
        this.scriptPath = ontrackScriptLocation == null ? null : ontrackScriptLocation.getScriptPath();
        this.scriptText = ontrackScriptLocation == null ? null : ontrackScriptLocation.getScriptText();
        this.sandbox = sandbox;
        this.injectEnvironment = injectEnvironment;
        this.injectProperties = injectProperties;
        this.ontrackLog = ontrackLog;
    }

    @SuppressWarnings("unused")
    public boolean isUsingText() {
        return usingText;
    }

    @SuppressWarnings("unused")
    public String getScriptPath() {
        return scriptPath;
    }

    @SuppressWarnings("unused")
    public String getScriptText() {
        return scriptText;
    }

    @SuppressWarnings("unused")
    public String getInjectEnvironment() {
        return injectEnvironment;
    }

    @SuppressWarnings("unused")
    public String getInjectProperties() {
        return injectProperties;
    }

    @SuppressWarnings("unused")
    public boolean isOntrackLog() {
        return ontrackLog;
    }

    public boolean isSandbox() {
        return sandbox;
    }

    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.BUILD;
    }

    @Override
    public boolean perform(AbstractBuild<?, ?> theBuild, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {
        // Reads the script text
        String script = OntrackPluginSupport.readScript(theBuild, usingText, scriptText, scriptPath);
        // Connector to Jenkins
        JenkinsConnector jenkins = new JenkinsConnector(theBuild, listener);
        // Ontrack DSL support
        OntrackDSLRunner dsl = OntrackDSLRunner.getRunnerForBuild(theBuild.getProject(), listener)
                .injectEnvironment(injectEnvironment, theBuild, listener)
                .injectProperties(injectProperties, theBuild, listener)
                .setSandbox(sandbox)
                // Connector to Jenkins
                .addBinding("jenkins", jenkins)
                // Output
                .addBinding("out", listener.getLogger());
        // Runs the script
        try {
            dsl.run(script);
        } catch (OTMessageClientException ex) {
            listener.getLogger().format("[ontrack] ERROR %s%n", ex.getMessage());
            theBuild.setResult(Result.FAILURE);
        } catch (OTHttpClientException ex) {
            listener.getLogger().format("[ontrack] ERROR %s%n", ex.getMessage());
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
