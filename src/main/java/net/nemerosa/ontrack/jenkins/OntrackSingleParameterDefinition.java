package net.nemerosa.ontrack.jenkins;

import hudson.Extension;
import hudson.model.ParameterValue;
import hudson.model.StringParameterValue;
import net.nemerosa.ontrack.jenkins.dsl.DSLRunner;
import net.sf.json.JSONObject;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;


public class OntrackSingleParameterDefinition extends AbstractOntrackParameterDefinition {

    @DataBoundConstructor
    public OntrackSingleParameterDefinition(String name, String description, String dsl, boolean sandbox, String valueProperty) {
        super(name, description, dsl, sandbox, valueProperty);
    }

    public OntrackSingleParameterDefinition(String name, String description, String dsl, boolean sandbox, String valueProperty, DSLRunner dslRunner) {
        super(name, description, dsl, sandbox, valueProperty, dslRunner);
    }

    /**
     * No input is expected for this computation.
     */
    @Override
    public ParameterValue createValue(StaplerRequest req, JSONObject jo) {
        return createValue();
    }

    /**
     * No input is expected for this computation.
     */
    @Override
    public ParameterValue createValue(StaplerRequest req) {
        return createValue();
    }

    protected ParameterValue createValue() {
        // Runs the DSL and gets its result
        Object any = runDSL();
        // Gets the value for this object
        String value = any != null ? getProperty(any, getValueProperty()) : "";
        // Returns the string as a parameter
        return new StringParameterValue(getName(), value, getDescription());
    }

    @Override
    public ParameterValue getDefaultParameterValue() {
        return createValue();
    }

    @Extension
    @Symbol("ontrackSingleParam")
    public static class DescriptorImpl extends ParameterDescriptor {

        @Override
        public String getDisplayName() {
            return "Ontrack: Single parameter";
        }

    }
}
