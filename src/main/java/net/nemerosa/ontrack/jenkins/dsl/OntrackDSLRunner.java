package net.nemerosa.ontrack.jenkins.dsl;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import net.nemerosa.ontrack.dsl.Ontrack;
import net.nemerosa.ontrack.dsl.OntrackLogger;

import java.util.Collections;
import java.util.Map;

public class OntrackDSLRunner implements DSLRunner {

    @Deprecated
    public static final OntrackDSLRunner INSTANCE = new OntrackDSLRunner();

    /**
     * Additional bindings to add to the list
     */
    private Map<String, Object> bindings = Collections.emptyMap();

    /**
     * Security enabled?
     */
    private boolean securityEnabled = true;

    /**
     * Sandbox execution?
     */
    private boolean sandbox = false;

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
        // Binding
        Binding binding = new Binding();
        binding.setProperty("ontrack", ontrack);
        // Groovy shell
        GroovyShell shell = new GroovyShell(binding);
        // Runs the script
        return shell.evaluate(dsl);
    }

}
