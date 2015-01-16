package net.nemerosa.ontrack.jenkins;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import hudson.model.ParameterDefinition;
import net.nemerosa.ontrack.dsl.Ontrack;
import net.nemerosa.ontrack.jenkins.dsl.OntrackDSLConnector;
import org.apache.commons.beanutils.BeanUtils;

public abstract class AbstractOntrackParameterDefinition extends ParameterDefinition {

    private final String dsl;
    private final String valueProperty;

    public AbstractOntrackParameterDefinition(String name, String description, String dsl, String valueProperty) {
        super(name, description);
        this.dsl = dsl;
        this.valueProperty = valueProperty;
    }

    public String getDsl() {
        return dsl;
    }

    public String getValueProperty() {
        return valueProperty;
    }

    protected Object runDSL() {
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

    protected String getProperty(Object any, String property) {
        try {
            return BeanUtils.getProperty(any, property);
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    String.format(
                            "Could not get property %s on object %s",
                            property,
                            any
                    )
            );
        }
    }

}
