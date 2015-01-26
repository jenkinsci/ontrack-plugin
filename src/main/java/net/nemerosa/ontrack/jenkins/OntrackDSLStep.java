package net.nemerosa.ontrack.jenkins;

import hudson.Extension;
import hudson.FilePath;
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

    @DataBoundConstructor
    public OntrackDSLStep(ScriptLocation scriptLocation, String injectEnvironment, String injectProperties, boolean ontrackLog) {
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
            theBuild.setResult(theBuild.getResult().combine(result));
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
