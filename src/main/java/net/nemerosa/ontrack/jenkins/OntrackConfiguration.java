package net.nemerosa.ontrack.jenkins;

import hudson.Extension;
import jenkins.model.GlobalConfiguration;
import jenkins.model.Jenkins;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.StaplerRequest;

@Extension
public class OntrackConfiguration extends GlobalConfiguration {

    public static OntrackConfiguration getOntrackConfiguration() {
        return (OntrackConfiguration) Jenkins.getInstance().getDescriptor(OntrackConfiguration.class);
    }

    private String ontrackUrl;
    private String ontrackUser;
    private String ontrackPassword;

    public OntrackConfiguration() {
        load();
    }

    @Override
    public boolean configure(StaplerRequest req, JSONObject json) throws FormException {
        ontrackUrl = json.getString("ontrackUrl");
        ontrackUser = json.getString("ontrackUser");
        ontrackPassword = json.getString("ontrackPassword");
        save();
        return super.configure(req, json);
    }

    public String getOntrackUrl() {
        return ontrackUrl;
    }

    public String getOntrackUser() {
        return ontrackUser;
    }

    public String getOntrackPassword() {
        return ontrackPassword;
    }

}
