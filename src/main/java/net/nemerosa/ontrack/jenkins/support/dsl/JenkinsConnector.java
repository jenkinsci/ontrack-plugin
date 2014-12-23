package net.nemerosa.ontrack.jenkins.support.dsl;

import hudson.model.Result;

import java.util.LinkedHashMap;
import java.util.Map;

public class JenkinsConnector {

    private final Map<String, String> env = new LinkedHashMap<String, String>();
    private final Result result;

    public JenkinsConnector(Result result) {
        this.result = result;
    }

    public void env(String name, String value) {
        env.put(name, value);
    }

    /**
     * Gets the current result of the build.
     */
    public Result getResult() {
        return result;
    }

    public Map<String, String> env() {
        return env;
    }

}
