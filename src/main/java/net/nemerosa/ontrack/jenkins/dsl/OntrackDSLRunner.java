package net.nemerosa.ontrack.jenkins.dsl;

import groovy.lang.Binding;
import hudson.model.Item;
import hudson.model.Run;
import hudson.model.TaskListener;
import jenkins.model.Jenkins;
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

    protected OntrackDSLRunner() {
    }

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

    public OntrackDSLRunner setSecurityEnabled(boolean securityEnabled) {
        this.securityEnabled = securityEnabled;
        return this;
    }

    public OntrackDSLRunner setSandbox(boolean sandbox) {
        this.sandbox = sandbox;
        return this;
    }

    public OntrackDSLRunner setLogging(boolean logging) {
        this.logging = logging;
        return this;
    }

    public OntrackDSLRunner setSource(Item source) {
        this.source = source;
        return this;
    }

    public OntrackDSLRunner setOntrackLogger(OntrackLogger ontrackLogger) {
        this.ontrackLogger = ontrackLogger;
        return this;
    }

    public OntrackDSLRunner setOntrackLogger(final TaskListener taskListener) {
        return setOntrackLogger(
                new OntrackLogger() {
                    @Override
                    public void trace(String message) {
                        taskListener.getLogger().println(message);
                    }
                }
        );
    }

    public OntrackDSLRunner injectEnvironment(String environmentVariables, Run run, TaskListener listener) throws IOException, InterruptedException {
        String[] names = environmentVariables.split(",");
        for (String name : names) {
            name = name.trim();
            String value = run.getEnvironment(listener).get(name, "");
            if (value != null) {
                bindings.put(name, value);
            }
        }
        return this;
    }

    public OntrackDSLRunner injectProperties(String propertyValues, Run run, TaskListener listener) throws IOException, InterruptedException {
        Map<String, String> properties = OntrackPluginSupport.parseProperties(propertyValues, run, listener);
        bindings.putAll(properties);
        return this;
    }

    public OntrackDSLRunner addBinding(String name, Object value) {
        bindings.put(name, value);
        return this;
    }

    /**
     * Creates a DSL runner for a build environment
     */
    public static OntrackDSLRunner getRunnerForBuild(Item run, TaskListener listener) {
        return new OntrackDSLRunner()
                .setSecurityEnabled(Jenkins.getInstance().isUseSecurity())
                .setOntrackLogger(listener)
                .setSource(run);
    }

    public static DSLRunner getRunner() {
        return new OntrackDSLRunner()
                .setSecurityEnabled(Jenkins.getInstance().isUseSecurity());
    }
}
