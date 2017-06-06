package net.nemerosa.ontrack.jenkins;

import hudson.Extension;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.model.Result;
import net.nemerosa.ontrack.dsl.http.OTHttpClientException;
import net.nemerosa.ontrack.dsl.http.OTMessageClientException;
import net.nemerosa.ontrack.jenkins.dsl.JenkinsConnector;
import net.nemerosa.ontrack.jenkins.dsl.OntrackDSLResult;
import net.nemerosa.ontrack.jenkins.dsl.OntrackDSLRunner;
import org.jenkins_ci.plugins.run_condition.RunCondition;
import org.kohsuke.stapler.DataBoundConstructor;

@SuppressWarnings("unused")
public class OntrackDSLRunCondition extends RunCondition {

    private final boolean usingText;
    private final String scriptPath;
    private final String scriptText;
    private final boolean sandbox;
    private final String injectEnvironment;
    private final String injectProperties;
    private final boolean ontrackLog;

    @DataBoundConstructor
    public OntrackDSLRunCondition(ScriptLocation ontrackScriptLocation, boolean sandbox, String injectEnvironment, String injectProperties, boolean ontrackLog) {
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
        // Connector to Jenkins
        JenkinsConnector jenkins = new JenkinsConnector(build, listener);
        // Ontrack DSL support
        OntrackDSLRunner dsl = OntrackDSLRunner.getRunnerForBuild(build.getProject(), listener)
                .injectEnvironment(injectEnvironment, build, listener)
                .injectProperties(injectProperties, build, listener)
                // Connector to Jenkins
                .setSandbox(sandbox)
                .addBinding("jenkins", jenkins)
                // Output
                .addBinding("out", listener.getLogger());
        // Runs the script
        try {
            Object dslResult = dsl.run(script);
            Result result = OntrackDSLResult.toJenkinsResult(dsl);
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
