package net.nemerosa.ontrack.jenkins;

import hudson.Extension;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.model.Result;
import net.nemerosa.ontrack.dsl.http.OTHttpClientException;
import net.nemerosa.ontrack.dsl.http.OTMessageClientException;
import net.nemerosa.ontrack.jenkins.dsl.OntrackDSL;
import net.nemerosa.ontrack.jenkins.dsl.OntrackDSLResult;
import org.jenkins_ci.plugins.run_condition.RunCondition;
import org.kohsuke.stapler.DataBoundConstructor;

public class OntrackDSLRunCondition extends RunCondition {

    private final boolean usingText;
    private final String scriptPath;
    private final String scriptText;
    private final String injectEnvironment;
    private final String injectProperties;
    private final boolean ontrackLog;

    @DataBoundConstructor
    public OntrackDSLRunCondition(ScriptLocation ontrackScriptLocation, String injectEnvironment, String injectProperties, boolean ontrackLog) {
        this.usingText = ontrackScriptLocation == null || ontrackScriptLocation.isUsingText();
        this.scriptPath = ontrackScriptLocation == null ? null : ontrackScriptLocation.getScriptPath();
        this.scriptText = ontrackScriptLocation == null ? null : ontrackScriptLocation.getScriptText();
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
    public boolean runPrebuild(AbstractBuild<?, ?> build, BuildListener listener) throws Exception {
        return true;
    }

    @Override
    public boolean runPerform(AbstractBuild<?, ?> build, BuildListener listener) throws Exception {
        return evaluate(build, listener);
    }

    protected boolean evaluate(AbstractBuild<?, ?> build, BuildListener listener) throws Exception {
        // Reads the script text
        String script = OntrackPluginSupport.readScript(build, usingText, scriptText, scriptPath);
        // Ontrack DSL support
        OntrackDSL dsl = new OntrackDSL(
                script,
                injectEnvironment,
                injectProperties,
                ontrackLog
        );
        // Runs the script
        try {
            OntrackDSLResult dslResult = dsl.run(build, listener);
            Result result = OntrackDSL.toJenkinsResult(dslResult.getShellResult());
            return result.equals(Result.SUCCESS);
        } catch (OTMessageClientException ex) {
            listener.getLogger().format("[ontrack] ERROR %s%n", ex.getMessage());
            return false;
        } catch (OTHttpClientException ex) {
            listener.getLogger().format("[ontrack] ERROR %s%n", ex.getMessage());
            if (ontrackLog) {
                ex.printStackTrace(listener.getLogger());
            }
            return false;
        }
    }

    @Extension
    public static class OntrackDSLRunConditionDescriptor extends RunConditionDescriptor {

        @Override
        public String getDisplayName() {
            return "Ontrack: DSL condition";
        }

    }
}
