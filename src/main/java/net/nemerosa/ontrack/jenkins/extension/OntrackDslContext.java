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

    /**
     * Using a path
     */
    public void path(String path) {
        scriptLocation = ScriptLocation.path(path);
    }

    /**
     * Using a script
     */
    public void script(String script) {
        scriptLocation = ScriptLocation.text(script);
    }

    /**
     * Injects environment variables
     */
    public void environment(String... names) {
        environment.addAll(Arrays.asList(names));
    }

    /**
     * Properties to inject
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
     */
    public void log(boolean log) {
        this.log = log;
    }

    /**
     * Computed location
     */
    public ScriptLocation getScriptLocation() {
        return scriptLocation;
    }

    /**
     * Computed environment
     */
    public String getInjectEnvironment() {
        return StringUtils.join(environment, ",");
    }

    /**
     * Properties definition
     */
    public String getInjectProperties() {
        return injectProperties;
    }

    /**
     * Logging
     */
    public boolean isLog() {
        return log;
    }
}
