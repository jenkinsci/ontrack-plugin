package net.nemerosa.ontrack.jenkins;

import hudson.Extension;
import jenkins.model.GlobalConfiguration;
import jenkins.model.Jenkins;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.StaplerRequest;

@Extension
public class OntrackConfiguration extends GlobalConfiguration {

    public static OntrackConfiguration getOntrackConfiguration() {
        Jenkins instance = Jenkins.getInstance();
        return (OntrackConfiguration) instance.getDescriptor(OntrackConfiguration.class);
    }

    private String ontrackConfigurationName;
    private String ontrackUrl;
    private String ontrackUser;
    private String ontrackPassword;
    private int ontrackMaxTries = 1;
    private int ontrackRetryDelaySeconds = 10000;

    public OntrackConfiguration() {
        load();
    }

    @Override
    public boolean configure(StaplerRequest req, JSONObject json) throws FormException {
        ontrackConfigurationName = json.getString("ontrackConfigurationName");
        ontrackUrl = json.getString("ontrackUrl");
        ontrackUser = json.getString("ontrackUser");
        ontrackPassword = json.getString("ontrackPassword");
        ontrackMaxTries = json.getInt("ontrackMaxTries");
        ontrackRetryDelaySeconds = json.getInt("ontrackRetryDelaySeconds");
        save();
        return super.configure(req, json);
    }

    public String getOntrackConfigurationName() {
        return ontrackConfigurationName;
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

    public void setOntrackConfigurationName(String ontrackConfigurationName) {
        this.ontrackConfigurationName = ontrackConfigurationName;
    }

    public void setOntrackUrl(String ontrackUrl) {
        this.ontrackUrl = ontrackUrl;
    }

    public void setOntrackUser(String ontrackUser) {
        this.ontrackUser = ontrackUser;
    }

    public void setOntrackPassword(String ontrackPassword) {
        this.ontrackPassword = ontrackPassword;
    }

    public int getOntrackMaxTries() {
        return ontrackMaxTries;
    }

    public void setOntrackMaxTries(int ontrackMaxTries) {
        this.ontrackMaxTries = ontrackMaxTries;
    }

    public int getOntrackRetryDelaySeconds() {
        return ontrackRetryDelaySeconds;
    }

    public void setOntrackRetryDelaySeconds(int ontrackRetryDelaySeconds) {
        this.ontrackRetryDelaySeconds = ontrackRetryDelaySeconds;
    }
}
