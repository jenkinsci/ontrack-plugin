package net.nemerosa.ontrack.jenkins;

import java.util.*;

public abstract class AbstractOntrackMultipleParameterDefinition extends AbstractOntrackParameterDefinition {

    public AbstractOntrackMultipleParameterDefinition(String name, String description, String dsl, boolean sandbox, String valueProperty, String injectProperties) {
        super(name, description, dsl, sandbox, valueProperty, injectProperties);
    }

    public AbstractOntrackMultipleParameterDefinition(String name, String description, String dsl, boolean sandbox, String valueProperty, Map<String, Object> bindings) {
        super(name, description, dsl, sandbox, valueProperty, bindings);
    }

    /**
     * List of possible values
     *
     * @return List of choices
     */
    public List<String> getChoices() {
        // Runs the DSL
        Object any = runDSL();
        // Collection of items
        Collection<?> items;
        // If not a collection, converts to it
        if (any instanceof Collection) {
            items = (Collection<?>) any;
        } else if (any != null) {
            items = Collections.singletonList(any);
        } else {
            items = Collections.emptyList();
        }
        // Gets the values
        List<String> values = new ArrayList<String>();
        // Collects the values
        for (Object item : items) {
            values.add(getProperty(item, getValueProperty()));
        }
        // OK
        return values;
    }

}
