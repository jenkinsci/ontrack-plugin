package net.nemerosa.ontrack.jenkins;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.*;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import net.nemerosa.ontrack.dsl.http.OTHttpClientException;
import net.nemerosa.ontrack.dsl.http.OTMessageClientException;
import net.nemerosa.ontrack.jenkins.dsl.JenkinsConnector;
import net.nemerosa.ontrack.jenkins.dsl.OntrackDSLResult;
import net.nemerosa.ontrack.jenkins.dsl.OntrackDSLRunner;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.IOException;
import java.util.Map;

/**
 * Step that allows to call the Ontrack DSL.
 */
public class OntrackDSLStep extends Builder {

    private final boolean usingText;
    private final String scriptPath;
    private final String scriptText;
    private final String injectEnvironment;
    private final String injectProperties;
    private final boolean ontrackLog;

    @DataBoundConstructor
    public OntrackDSLStep(ScriptLocation ontrackScriptLocation, String injectEnvironment, String injectProperties, boolean ontrackLog) {
        this.usingText = ontrackScriptLocation == null || ontrackScriptLocation.isUsingText();
        this.scriptPath = ontrackScriptLocation == null ? null : ontrackScriptLocation.getScriptPath();
        this.scriptText = ontrackScriptLocation == null ? null : ontrackScriptLocation.getScriptText();
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
                .setLogging(ontrackLog)
                // Jenkins connector
                .addBinding("jenkins", jenkins)
                // Output
                .addBinding("out", listener.getLogger());
        // Runs the script
        try {
            Object dslResult = dsl.run(script);
            // Result
            Result result = OntrackDSLResult.toJenkinsResult(dslResult);
            listener.getLogger().format("Ontrack DSL script result evaluated to %s%n", result);
            setBuildResult(theBuild, result);
            // Environment
            for (Map.Entry<String, String> entry : jenkins.env().entrySet()) {
                String name = entry.getKey();
                String value = entry.getValue();
                listener.getLogger().format("Ontrack DSL: setting %s = %s%n", name, value);
                theBuild.addAction(new ParametersAction(new StringParameterValue(
                        name,
                        value
                )));
            }
        } catch (OTMessageClientException ex) {
            listener.getLogger().format("[ontrack] ERROR %s%n", ex.getMessage());
            setBuildResult(theBuild, Result.FAILURE);
        } catch (OTHttpClientException ex) {
            listener.getLogger().format("[ontrack] ERROR %s%n", ex.getMessage());
            if (ontrackLog) {
                ex.printStackTrace(listener.getLogger());
            }
            setBuildResult(theBuild, Result.FAILURE);
        }
        // End
        return true;
    }

    private void setBuildResult(AbstractBuild<?, ?> theBuild, Result result) {
        Result currentResult = theBuild.getResult();
        if (currentResult != null) {
            theBuild.setResult(currentResult.combine(result));
        } else {
            theBuild.setResult(result);
        }
    }

    @Extension
    public static class OntrackDSLStepDescription extends BuildStepDescriptor<Builder> {


        @Override
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return "Ontrack: DSL";
        }
    }
}
