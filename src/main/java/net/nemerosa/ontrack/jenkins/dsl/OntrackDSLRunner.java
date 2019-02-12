package net.nemerosa.ontrack.jenkins.dsl;

import groovy.lang.Binding;
import hudson.model.Item;
import hudson.model.Run;
import hudson.model.TaskListener;
import jenkins.model.Jenkins;
import net.nemerosa.ontrack.dsl.Ontrack;
import net.nemerosa.ontrack.dsl.OntrackLogger;
import net.nemerosa.ontrack.jenkins.OntrackConfiguration;
import net.nemerosa.ontrack.jenkins.OntrackPluginSupport;
import net.nemerosa.ontrack.jenkins.OntrackSecurityMode;

import javax.annotation.CheckForNull;
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

        // Gets the configured security mode
        OntrackSecurityMode securityMode = getSecurityMode();

        // Launcher
        DSLLauncher launcher;

        // No specific setup or default mode
        if (securityMode == null || securityMode == OntrackSecurityMode.DEFAULT) {
            // Security enabled
            if (securityEnabled) {
                // Using a sandbox
                if (sandbox) {
                    launcher = new SandboxDSLLauncher(source);
                }
                // Using approvals
                else {
                    launcher = new ApprovalBasedDSLLauncher(source);
                }
            }
            // No security, using defaults
            else {
                launcher = new DefaultDSLLauncher();
            }
        }
        // No security
        else if (securityMode == OntrackSecurityMode.NONE) {
            launcher = new DefaultDSLLauncher();
        }
        // Sandbox
        else if (securityMode == OntrackSecurityMode.SANDBOX) {
            launcher = new SandboxDSLLauncher(source);
        }
        // Anomaly
        else {
            throw new IllegalStateException("Unknown Ontrack security mode: " + securityMode);
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

    public OntrackDSLRunner injectProperties(@CheckForNull String propertyValues, @CheckForNull Run run, @CheckForNull TaskListener listener) throws IOException, InterruptedException {
        if (propertyValues != null) {
            Map<String, String> properties = OntrackPluginSupport.parseProperties(propertyValues, run, listener);
            bindings.putAll(properties);
        }
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
                .setSecurityEnabled(isUseSecurity())
                .setOntrackLogger(listener)
                .setSource(run);
    }

    private static boolean isUseSecurity() {
        Jenkins jenkins = Jenkins.getInstanceOrNull();
        return jenkins != null && jenkins.isUseSecurity();
    }

    private static OntrackSecurityMode getSecurityMode() {
        OntrackConfiguration ontrackConfiguration = OntrackConfiguration.getOntrackConfiguration();
        OntrackSecurityMode securityMode = null;
        if (ontrackConfiguration != null) {
            securityMode = ontrackConfiguration.getSecurityMode();
        }
        return securityMode;
    }

    public static OntrackDSLRunner getRunner() {
        return new OntrackDSLRunner()
                .setSecurityEnabled(isUseSecurity());
    }
}
