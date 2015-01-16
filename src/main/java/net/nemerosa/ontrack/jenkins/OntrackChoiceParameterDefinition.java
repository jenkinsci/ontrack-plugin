package net.nemerosa.ontrack.jenkins;

import hudson.Extension;
import hudson.model.ParameterValue;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

public class OntrackChoiceParameterDefinition extends AbstractOntrackMultipleParameterDefinition {

    @DataBoundConstructor
    public OntrackChoiceParameterDefinition(String name, String description, String dsl, String valueProperty, String labelProperty) {
        super(name, description, dsl, valueProperty, labelProperty);
    }

    @Override
    public ParameterValue createValue(StaplerRequest req, JSONObject jo) {
        // FIXME Method net.nemerosa.ontrack.jenkins.OntrackChoiceParameterDefinition.createValue
        return null;
    }

    @Override
    public ParameterValue createValue(StaplerRequest req) {
        // FIXME Method net.nemerosa.ontrack.jenkins.OntrackChoiceParameterDefinition.createValue
        return null;
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
}
