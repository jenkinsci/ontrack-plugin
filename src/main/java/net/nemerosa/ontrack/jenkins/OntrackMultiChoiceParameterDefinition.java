package net.nemerosa.ontrack.jenkins;

import hudson.Extension;
import hudson.model.ParameterValue;
import hudson.model.StringParameterValue;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import java.util.ArrayList;
import java.util.List;

public class OntrackMultiChoiceParameterDefinition extends AbstractOntrackMultipleParameterDefinition {

    @DataBoundConstructor
    public OntrackMultiChoiceParameterDefinition(String name, String description, String dsl, String valueProperty) {
        super(name, description, dsl, valueProperty);
    }

    @Override
    public ParameterValue createValue(StaplerRequest req, JSONObject jo) {
        JSONArray jsonArray = (JSONArray)jo.get("value");
        List<String> choices = getChoices();
        List<String> selectionList = new ArrayList<>();
        for(int i=0;i<choices.size();i++){
            String hostName = choices.get(i);
            Object checked = jsonArray.get(i);
            Boolean checkedBoolean = (Boolean)checked;
            if(checkedBoolean){
                selectionList.add(hostName);
            }
        }
        String selections = StringUtils.join(selectionList, ',');
        StringParameterValue value = new StringParameterValue(getName(), selections, getDescription());
        return value;
    }

    @Override
//    @Deprecated ???
    public ParameterValue createValue(StaplerRequest req) {
        String[] value = req.getParameterValues(getName());
        String selections = StringUtils.join(value, ',');
        return new StringParameterValue(getName(), selections, getDescription());
    }

    @Override
    public StringParameterValue getDefaultParameterValue() {
        return new StringParameterValue(getName(), "", getDescription());
    }

    @Extension
    public static class DescriptorImpl extends ParameterDescriptor {

        @Override
        public String getDisplayName() {
            return "Ontrack: Multi Parameter choice";
        }

        @Override
        public String getHelpFile() {
            return "/help/ontrack/parameter-choice.html";
        }
    }
}
