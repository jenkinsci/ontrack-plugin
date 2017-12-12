package net.nemerosa.ontrack.jenkins;

import hudson.Extension;
import hudson.model.ParameterValue;
import hudson.model.StringParameterValue;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import java.util.List;

public class OntrackChoiceParameterDefinition extends AbstractOntrackMultipleParameterDefinition {

    private final boolean multiSelect;

    @DataBoundConstructor
    public OntrackChoiceParameterDefinition(String name, String description, String dsl, String valueProperty) {
        this(name,description,dsl,valueProperty,false);
    }

    @DataBoundConstructor
    public OntrackChoiceParameterDefinition(String name, String description, String dsl, String valueProperty, boolean multiSelect) {
        super(name, description, dsl, valueProperty);
        this.multiSelect = multiSelect;
    }

    @Override
    public ParameterValue createValue(StaplerRequest req, JSONObject jo) {
        StringParameterValue value = req.bindJSON(StringParameterValue.class, jo);
        value.setDescription(getDescription());
        return value;
    }

    @Override
    public ParameterValue createValue(StaplerRequest req) {
        String[] value = req.getParameterValues(getName());
        if (value == null) {
            return getDefaultParameterValue();
        } else if (value.length != 1) {
            throw new IllegalArgumentException(String.format(
                    "Illegal number of parameter values for %s: %d",
                    getName(),
                    value.length));
        } else {
            List<String> choices = getChoices();
            if (choices.contains(value[0])) {
                return new StringParameterValue(getName(), value[0], getDescription());
            } else {
                throw new IllegalArgumentException(String.format(
                        "Value %s for parameter %s is not a valid choice.",
                        value[0],
                        getName()));
            }
        }
    }

    @Override
    public StringParameterValue getDefaultParameterValue() {
        List<String> choices = getChoices();
        String value = choices.isEmpty() ? "" : choices.get(0);
        return new StringParameterValue(getName(), value, getDescription());
    }

    @Extension
    public static class DescriptorImpl extends ParameterDescriptor {

        @Override
        public String getDisplayName() {
            return "Ontrack: Parameter choice";
        }

        @Override
        public String getHelpFile() {
            return "/help/ontrack/parameter-choice.html";
        }
    }

    public boolean isMultiSelect() {
        return multiSelect;
    }
}
