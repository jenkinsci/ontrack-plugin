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
import net.nemerosa.ontrack.client.ClientException;
import net.nemerosa.ontrack.client.OTHttpClientLogger;
import net.nemerosa.ontrack.dsl.Ontrack;
import net.nemerosa.ontrack.dsl.OntrackConnection;
import net.nemerosa.ontrack.dsl.ProjectEntity;
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
        // Connection to Ontrack
        Ontrack ontrack = createOntrackConnector(listener);
        // Connector to Jenkins
        JenkinsConnector jenkins = new JenkinsConnector();
        // Values to bind
        Map<String, Object> values = new HashMap<String, Object>();
        // Gets the environment
        String[] names = injectEnvironment.split(",");
        for (String name : names) {
            name = name.trim();
            String value = theBuild.getEnvironment(listener).get(name, "");
            if (value != null) {
                values.put(name, value);
            }
        }
        // TODO Gets the properties
        // Binding
        values.put("ontrack", ontrack);
        values.put("jenkins", jenkins);
        Binding binding = new Binding(values);
        // Groovy shell
        GroovyShell shell = new GroovyShell(binding);
        // Runs the script
        try {
            Object shellResult = shell.evaluate(script);
            if (ontrackLog) {
                listener.getLogger().format("Ontrack DSL script returned result: %s%n", shellResult);
            } else {
                listener.getLogger().format("Ontrack DSL script returned result.%n");
            }
            // Result
            Result result = toJenkinsResult(shellResult);
            listener.getLogger().format("Ontrack DSL script result evaluated to %s%n", result);
            setBuildResult(theBuild, result);
            // TODO Environment
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

    private Result toJenkinsResult(Object shellResult) {
        if (shellResult == null ||
                shellResult.equals(0) ||
                shellResult.equals(false) ||
                shellResult.equals("") ||
                shellResult instanceof ProjectEntity) {
            return Result.SUCCESS;
        } else {
            return Result.FAILURE;
        }
    }

    private Ontrack createOntrackConnector(final BuildListener listener) {
        OntrackConfiguration config = OntrackConfiguration.getOntrackConfiguration();
        OntrackConnection connection = OntrackConnection.create(config.getOntrackUrl());
        // Logging
        if (ontrackLog) {
            connection = connection.logger(new OTHttpClientLogger() {
                public void trace(String message) {
                    listener.getLogger().println(message);
                }
            });
        }
        // Authentication
        String user = config.getOntrackUser();
        if (StringUtils.isNotBlank(user)) {
            connection = connection.authenticate(
                    user,
                    config.getOntrackPassword()
            );
        }
        // Building the Ontrack root
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
