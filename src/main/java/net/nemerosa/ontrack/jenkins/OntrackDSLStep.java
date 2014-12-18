package net.nemerosa.ontrack.jenkins;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import net.nemerosa.ontrack.dsl.Ontrack;
import net.nemerosa.ontrack.dsl.OntrackConnection;
import net.nemerosa.ontrack.jenkins.support.JenkinsConnector;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Step that allows to call the Ontrack DSL.
 */
public class OntrackDSLStep extends Builder {

    private final String script;
    private final boolean injectEnvironment;
    private final String injectProperties;

    @DataBoundConstructor
    public OntrackDSLStep(String script, boolean injectEnvironment, String injectProperties) {
        this.script = script;
        this.injectEnvironment = injectEnvironment;
        this.injectProperties = injectProperties;
    }

    public String getScript() {
        return script;
    }

    public boolean isInjectEnvironment() {
        return injectEnvironment;
    }

    public String getInjectProperties() {
        return injectProperties;
    }

    @Override
    public boolean perform(AbstractBuild<?, ?> theBuild, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {
        // Connection to Ontrack
        Ontrack ontrack = createOntrackConnector();
        // Connector to Jenkins
        JenkinsConnector jenkins = new JenkinsConnector();
        // Values to bind
        Map<String, Object> values = new HashMap<String, Object>();
        // TODO Gets the environment
        // TODO Gets the properties
        // Binding
        values.put("ontrack", ontrack);
        values.put("jenkins", jenkins);
        Binding binding = new Binding(values);
        // Groovy shell
        GroovyShell shell = new GroovyShell(binding);
        // TODO Groovy sandbox in Jenkins?
        // Runs the script
        Object shellResult = shell.evaluate(script);
        listener.getLogger().format("Ontrack DSL script returned result: %s", shellResult);
        // Result
        Result result = toJenkinsResult(shellResult);
        listener.getLogger().format("Ontrack DSL script result evaluated to %s", result);
        theBuild.setResult(theBuild.getResult().combine(Result.FAILURE));
        // TODO Environment
        // End
        return true;
    }

    private Result toJenkinsResult(Object shellResult) {
        if (shellResult == null ||
                shellResult.equals(0) ||
                shellResult.equals(false) ||
                shellResult.equals("")) {
            return Result.SUCCESS;
        } else {
            return Result.FAILURE;
        }
    }

    private Ontrack createOntrackConnector() {
        OntrackConfiguration config = OntrackConfiguration.getOntrackConfiguration();
        OntrackConnection connection = OntrackConnection.create(config.getOntrackUrl());
        String user = config.getOntrackUser();
        if (StringUtils.isNotBlank(user)) {
            connection = connection.authenticate(
                    user,
                    config.getOntrackPassword()
            );
        }
        return connection.build();
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
