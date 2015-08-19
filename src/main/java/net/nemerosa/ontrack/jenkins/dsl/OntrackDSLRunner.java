package net.nemerosa.ontrack.jenkins.dsl;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import net.nemerosa.ontrack.dsl.Ontrack;

public class OntrackDSLRunner implements DSLRunner {

    @Override
    public Object run(String dsl) {
        // Connection to Ontrack
        Ontrack ontrack = OntrackDSLConnector.createOntrackConnector(System.out);
        // Binding
        Binding binding = new Binding();
        binding.setProperty("ontrack", ontrack);
        // Groovy shell
        GroovyShell shell = new GroovyShell(binding);
        // Runs the script
        return shell.evaluate(dsl);
    }

}
