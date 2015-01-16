package net.nemerosa.ontrack.jenkins;

import hudson.model.ParameterDefinition;
import hudson.model.Result;
import net.nemerosa.ontrack.client.ClientException;
import net.nemerosa.ontrack.jenkins.dsl.OntrackDSL;
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
        // FIXME Runs the DSL
        return null;
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
