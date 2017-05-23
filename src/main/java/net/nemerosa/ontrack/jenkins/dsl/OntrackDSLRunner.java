package net.nemerosa.ontrack.jenkins.dsl;

import groovy.lang.Binding;
import hudson.model.Item;
import hudson.model.Run;
import hudson.model.TaskListener;
import net.nemerosa.ontrack.dsl.Ontrack;
import net.nemerosa.ontrack.dsl.OntrackLogger;
import net.nemerosa.ontrack.jenkins.OntrackPluginSupport;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class OntrackDSLRunner implements DSLRunner {

    /**
     * Additional bindings to add to the list
     */
    private Map<String, Object> bindings = new HashMap<>();

    /**
     * Security enabled?
     */
    private boolean securityEnabled = true;

    /**
     * Sandbox execution?
     */
    private boolean sandbox = false;

    /**
     * Logging enabled
     */
    private boolean logging = false;

    /**
     * Source job
     */
    private Item source;

    /**
     * Logger
     */
    private OntrackLogger ontrackLogger = new OntrackLogger() {
        @Override
        public void trace(String message) {
            System.out.println(message);
        }
    };

    @Override
    public Object run(String dsl) {
        // Connection to Ontrack
        Ontrack ontrack = OntrackDSLConnector.createOntrackConnector(ontrackLogger);

        // Launcher
        DSLLauncher launcher;
        // Security enabled
        if (securityEnabled) {
            // Using a sandbox
            if (sandbox) {
                launcher = new SandboxDSLLauncher(source);
            }
            // Using approvals
            else {
                launcher = new ApprovalBasedDSLLauncher();
            }
        }
        // No security, using defaults
        else {
            launcher = new DefaultDSLLauncher();
        }

        // Binding
        Binding binding = new Binding(bindings);
        binding.setProperty("ontrack", ontrack);

        // Runs the script
        ontrackLogger.trace(String.format("Ontrack DSL script running with launcher %s...%n", launcher.getClass().getName()));
        Object shellResult = launcher.run(dsl, binding);
        if (logging) {
            ontrackLogger.trace(String.format("Ontrack DSL script returned result: %s%n", shellResult));
        } else {
            ontrackLogger.trace(String.format("Ontrack DSL script returned result.%n"));
        }
        // Runs the script
        return shellResult;
    }

    public void setBindings(Map<String, Object> bindings) {
        this.bindings = bindings;
    }

    public void setSecurityEnabled(boolean securityEnabled) {
        this.securityEnabled = securityEnabled;
    }

    public void setSandbox(boolean sandbox) {
        this.sandbox = sandbox;
    }

    public void setLogging(boolean logging) {
        this.logging = logging;
    }

    public void setOntrackLogger(OntrackLogger ontrackLogger) {
        this.ontrackLogger = ontrackLogger;
    }

    public void setOntrackLogger(final TaskListener taskListener) {
        setOntrackLogger(
                new OntrackLogger() {
                    @Override
                    public void trace(String message) {
                        taskListener.getLogger().println(message);
                    }
                }
        );
    }

    public void injectEnvironment(String environmentVariables, Run run, TaskListener listener) throws IOException, InterruptedException {
        String[] names = environmentVariables.split(",");
        for (String name : names) {
            name = name.trim();
            String value = run.getEnvironment(listener).get(name, "");
            if (value != null) {
                bindings.put(name, value);
            }
        }
    }

    public void injectProperties(String propertyValues, Run run, TaskListener listener) throws IOException, InterruptedException {
        Map<String, String> properties = OntrackPluginSupport.parseProperties(propertyValues, run, listener);
        bindings.putAll(properties);
    }

    public void addBinding(String name, Object value) {
        bindings.put(name, value);
    }
}
