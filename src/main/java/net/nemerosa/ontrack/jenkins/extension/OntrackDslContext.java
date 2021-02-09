package net.nemerosa.ontrack.jenkins.extension;

import javaposse.jobdsl.dsl.Context;
import net.nemerosa.ontrack.jenkins.ScriptLocation;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OntrackDslContext implements Context {

    private ScriptLocation scriptLocation = ScriptLocation.text("");
    private List<String> environment = new ArrayList<String>();
    private String injectProperties = "";
    private boolean log = false;
    private boolean sandbox = false;
    private boolean ignoreFailure = false;

    /**
     * Using a path
     *
     * @param path Path to the script
     */
    public void path(String path) {
        scriptLocation = ScriptLocation.path(path);
    }

    /**
     * Using a script
     *
     * @param script Script content
     */
    public void script(String script) {
        scriptLocation = ScriptLocation.text(script);
    }

    /**
     * Injects environment variables
     *
     * @param names List of environment variables
     */
    public void environment(String... names) {
        environment.addAll(Arrays.asList(names));
    }

    /**
     * Properties to inject
     *
     * @param properties Properties to inject
     */
    public void properties(String properties) {
        injectProperties = properties;
    }

    /**
     * Enables the log
     */
    public void log() {
        log(true);
    }

    /**
     * Enables or disables the log
     *
     * @param log Enabling or disabling logging
     */
    public void log(boolean log) {
        this.log = log;
    }

    /**
     * Enables the sandbox
     */
    public void sandbox() {
        sandbox(true);
    }

    /**
     * Enables or disables the sandbox
     *
     * @param sandbox Enabling or disabling the sandbox
     */
    public void sandbox(boolean sandbox) {
        this.sandbox = sandbox;
    }

    /**
     * Computed location
     *
     * @return Script location
     */
    public ScriptLocation getScriptLocation() {
        return scriptLocation;
    }

    /**
     * Computed environment
     *
     * @return Environment to inject
     */
    public String getInjectEnvironment() {
        return StringUtils.join(environment, ",");
    }

    /**
     * Properties definition
     *
     * @return Properties to inject
     */
    public String getInjectProperties() {
        return injectProperties;
    }

    /**
     * Logging
     *
     * @return Is logging enabled?
     */
    public boolean isLog() {
        return log;
    }

    /**
     * Sandbox
     *
     * @return Is sandbox enabled?
     */
    public boolean isSandbox() {
        return sandbox;
    }

    /**
     * Ignoring failure
     *
     * @return Are failures ignored?
     */
    public boolean isIgnoreFailure() {
        return ignoreFailure;
    }

    /**
     * Ignoring failure
     */
    public void ignoreFailure() {
        ignoreFailure(true);
    }

    /**
     * Ignoring failure
     *
     * @param value If failures must be ignored
     */
    public void ignoreFailure(boolean value) {
        ignoreFailure = value;
    }

}
