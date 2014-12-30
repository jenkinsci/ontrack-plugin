package net.nemerosa.ontrack.jenkins;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.*;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import net.nemerosa.ontrack.client.ClientException;
import net.nemerosa.ontrack.jenkins.dsl.OntrackDSL;
import net.nemerosa.ontrack.jenkins.dsl.OntrackDSLResult;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.IOException;
import java.util.Map;

/**
 * Step that allows to call the Ontrack DSL.
 */
public class OntrackDSLStep extends Builder {

    private final String script;
    private final String injectEnvironment;
    private final String injectProperties;
    private final boolean ontrackLog;

    @DataBoundConstructor
    public OntrackDSLStep(String script, String injectEnvironment, String injectProperties, boolean ontrackLog) {
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
        } catch (ClientException ex) {
            listener.getLogger().format("Ontrack DSL script failed with:%n%s%n", ex.getMessage());
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
