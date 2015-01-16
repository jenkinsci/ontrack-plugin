package net.nemerosa.ontrack.jenkins;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public abstract class AbstractOntrackMultipleParameterDefinition extends AbstractOntrackParameterDefinition {

    public AbstractOntrackMultipleParameterDefinition(String name, String description, String dsl, String valueProperty) {
        super(name, description, dsl, valueProperty);
    }

    /**
     * List of possible values
     */
    public List<String> getChoices() {
        // Runs the DSL
        Object any = runDSL();
        // Collection of items
        Collection<?> items;
        // If not a collection, converts to it
        if (any instanceof Collection) {
            items = (Collection<?>) any;
        } else {
            items = Collections.singletonList(any);
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
