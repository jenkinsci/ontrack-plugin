package net.nemerosa.ontrack.jenkins;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.*;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import net.nemerosa.ontrack.dsl.http.OTHttpClientException;
import net.nemerosa.ontrack.dsl.http.OTMessageClientException;
import net.nemerosa.ontrack.jenkins.dsl.OntrackDSL;
import net.nemerosa.ontrack.jenkins.dsl.OntrackDSLResult;
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
    private final boolean ignoreFailure;

    @DataBoundConstructor
    public OntrackDSLStep(ScriptLocation ontrackScriptLocation, String injectEnvironment, String injectProperties, boolean ontrackLog, boolean ignoreFailure) {
        this.usingText = ontrackScriptLocation == null || ontrackScriptLocation.isUsingText();
        this.scriptPath = ontrackScriptLocation == null ? null : ontrackScriptLocation.getScriptPath();
        this.scriptText = ontrackScriptLocation == null ? null : ontrackScriptLocation.getScriptText();
        this.injectEnvironment = injectEnvironment;
        this.injectProperties = injectProperties;
        this.ontrackLog = ontrackLog;
        this.ignoreFailure = ignoreFailure;
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

    public boolean isIgnoreFailure() {
        return ignoreFailure;
    }

    @Override
    public boolean perform(AbstractBuild<?, ?> theBuild, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {
        // Reads the script text
        String script = OntrackPluginSupport.readScript(theBuild, usingText, scriptText, scriptPath);
        // Ontrack DSL support
        OntrackDSL dsl = new OntrackDSL(
                script,
                injectEnvironment,
                injectProperties,
                ontrackLog
        );
        // Runs the script
        try {
            OntrackDSLResult dslResult = dsl.run(theBuild, listener);
            // Result
            Result result = OntrackDSL.toJenkinsResult(dslResult.getShellResult());
            listener.getLogger().format("Ontrack DSL script result evaluated to %s%n", result);
            setBuildResult(theBuild, result);
            // Environment
            for (Map.Entry<String, String> entry : dslResult.getConnector().env().entrySet()) {
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
            if (!ignoreFailure) {
                setBuildResult(theBuild, Result.FAILURE);
            }
        } catch (OTHttpClientException ex) {
            listener.getLogger().format("[ontrack] ERROR %s%n", ex.getMessage());
            if (ontrackLog) {
                ex.printStackTrace(listener.getLogger());
            }
            if (!ignoreFailure) {
                setBuildResult(theBuild, Result.FAILURE);
            }
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
