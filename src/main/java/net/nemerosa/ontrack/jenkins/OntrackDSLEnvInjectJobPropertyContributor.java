package net.nemerosa.ontrack.jenkins;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import hudson.Extension;
import hudson.model.AbstractBuild;
import hudson.model.TaskListener;
import jenkins.model.Jenkins;
import net.nemerosa.ontrack.dsl.Ontrack;
import net.nemerosa.ontrack.jenkins.dsl.OntrackDSLConnector;
import org.apache.commons.lang.StringUtils;
import org.jenkinsci.lib.envinject.EnvInjectException;
import org.jenkinsci.plugins.envinject.model.EnvInjectJobPropertyContributor;
import org.jenkinsci.plugins.envinject.model.EnvInjectJobPropertyContributorDescriptor;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unused")
public class OntrackDSLEnvInjectJobPropertyContributor extends EnvInjectJobPropertyContributor {

    private String scriptText;
    private boolean ontrackLog;

    @SuppressWarnings("unused")
    public OntrackDSLEnvInjectJobPropertyContributor() {
    }

    @DataBoundConstructor
    public OntrackDSLEnvInjectJobPropertyContributor(String scriptText, boolean ontrackLog) {
        this.scriptText = scriptText;
        this.ontrackLog = ontrackLog;
    }

    public String getScriptText() {
        return scriptText;
    }

    public boolean isOntrackLog() {
        return ontrackLog;
    }

    @Override
    public void init() {
    }

    @Override
    public Map<String, String> getEnvVars(AbstractBuild build, TaskListener listener) throws EnvInjectException {
        // No contribution if no script
        if (StringUtils.isBlank(scriptText)) {
            return Collections.emptyMap();
        }
        // Ontrack connection
        Ontrack ontrack = OntrackDSLConnector.createOntrackConnector(ontrackLog ? listener.getLogger() : null);
        // Values to bind
        Map<String, Object> values = new HashMap<String, Object>();
        // Binding
        values.put("ontrack", ontrack);
        values.put("jenkins", Jenkins.getInstanceOrNull());
        values.put("build", build);
        values.put("out", listener.getLogger());
        try {
            values.put("env", build.getEnvironment(listener));
        } catch (IOException e) {
            throw new EnvInjectException("Cannot inject environment in DSL", e);
        } catch (InterruptedException e) {
            throw new EnvInjectException("Cannot inject environment in DSL", e);
        }
        Binding binding = new Binding(values);
        // Groovy shell
        GroovyShell shell = new GroovyShell(binding);
        // Runs the script
        listener.getLogger().format("Ontrack DSL script running...%n");
        Object result = shell.evaluate(scriptText);
        if (ontrackLog) {
            listener.getLogger().format("Ontrack DSL script returned result: %s%n", result);
        } else {
            listener.getLogger().format("Ontrack DSL script returned result.%n");
        }
        // Result
        if (result instanceof Map) {
            Map<String, String> env = new HashMap<String, String>();
            //noinspection unchecked
            for (Map.Entry<String, ?> entry : ((Map<String, ?>) result).entrySet()) {
                String name = entry.getKey();
                Object value = entry.getValue();
                if (value != null) {
                    listener.getLogger().format("Ontrack DSL environment: %s -> %s.%n", name, value);
                    env.put(name, String.valueOf(value));
                }
            }
            // OK
            return env;
        } else {
            listener.getLogger().format("The Ontrack DSL didn't return an environment map.");
            return Collections.emptyMap();
        }
    }

    @Extension
    public static class OntrackDSLEnvInjectJobPropertyContributorDescriptor extends EnvInjectJobPropertyContributorDescriptor {

        @Override
        public String getDisplayName() {
            return "Ontrack: injecting environment variables from DSL";
        }
    }

}
