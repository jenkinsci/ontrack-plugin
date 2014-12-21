package net.nemerosa.ontrack.jenkins.support;

import java.util.LinkedHashMap;
import java.util.Map;

public class JenkinsConnector {

    private final Map<String, String> env = new LinkedHashMap<String, String>();

    public void env(String name, String value) {
        env.put(name, value);
    }

    public Map<String, String> env() {
        return env;
    }

}
