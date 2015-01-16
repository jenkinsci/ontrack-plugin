package net.nemerosa.ontrack.jenkins;

public abstract class AbstractOntrackMultipleParameterDefinition extends AbstractOntrackParameterDefinition {

    private final String labelProperty;

    public AbstractOntrackMultipleParameterDefinition(String name, String description, String dsl, String valueProperty, String labelProperty) {
        super(name, description, dsl, valueProperty);
        this.labelProperty = labelProperty;
    }

    public String getLabelProperty() {
        return labelProperty;
    }
}
