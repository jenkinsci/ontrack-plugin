package net.nemerosa.ontrack.jenkins;

import hudson.Extension;
import hudson.util.ListBoxModel;
import jenkins.model.GlobalConfiguration;
import jenkins.model.Jenkins;
import net.nemerosa.ontrack.dsl.OntrackConnection;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.StaplerRequest;

import javax.annotation.Nullable;
import java.util.concurrent.atomic.AtomicReference;

@Extension
public class OntrackConfiguration extends GlobalConfiguration {

    public static @Nullable
    OntrackConfiguration getOntrackConfiguration() {
        Jenkins instance = Jenkins.getInstanceOrNull();
        return instance != null ? (OntrackConfiguration) instance.getDescriptor(OntrackConfiguration.class) : null;
    }

    private String ontrackConfigurationName;
    private String ontrackUrl;
    private String ontrackUser;
    private String ontrackPassword;
    private int ontrackMaxTries = 1;
    private int ontrackRetryDelaySeconds = 10000;
    private OntrackSecurityMode securityMode = OntrackSecurityMode.DEFAULT;
    private final AtomicReference<Version> version = new AtomicReference<>();

    public OntrackConfiguration() {
        load();
    }

    @Override
    public synchronized void load() {
        super.load();
        loadVersion();
    }

    @Override
    public boolean configure(StaplerRequest req, JSONObject json) throws FormException {
        ontrackConfigurationName = json.getString("ontrackConfigurationName");
        ontrackUrl = json.getString("ontrackUrl");
        ontrackUser = json.getString("ontrackUser");
        ontrackPassword = json.getString("ontrackPassword");
        ontrackMaxTries = json.getInt("ontrackMaxTries");
        ontrackRetryDelaySeconds = json.getInt("ontrackRetryDelaySeconds");
        securityMode = OntrackSecurityMode.valueOf(json.getString("securityMode"));
        save();
        boolean ok = super.configure(req, json);
        // Getting the version from Ontrack
        loadVersion();
        // OK
        return ok;
    }

    private void loadVersion() {
        version.updateAndGet(
                ignored -> computeVersion()
        );
    }

    private Version computeVersion() {
        try {
            OntrackConnection connection = OntrackConnection.create(ontrackUrl);
            String user = ontrackUser;
            if (StringUtils.isNotBlank(user)) {
                connection = connection.authenticate(
                        user,
                        ontrackPassword
                );
            }
            String versionString = connection.build().getVersion();
            return Version.of(versionString);
        } catch (Exception ignored) {
            return null;
        }
    }

    public @Nullable
    Version getVersion() {
        return version.updateAndGet(
                currentVersion -> currentVersion != null ? currentVersion : computeVersion()
        );
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

    public OntrackSecurityMode getSecurityMode() {
        return securityMode;
    }

    @SuppressWarnings("unused")
    public void setSecurityMode(OntrackSecurityMode securityMode) {
        this.securityMode = securityMode;
    }

    @SuppressWarnings("unused")
    public void setOntrackConfigurationName(String ontrackConfigurationName) {
        this.ontrackConfigurationName = ontrackConfigurationName;
    }

    @SuppressWarnings("unused")
    public void setOntrackUrl(String ontrackUrl) {
        this.ontrackUrl = ontrackUrl;
    }

    @SuppressWarnings("unused")
    public void setOntrackUser(String ontrackUser) {
        this.ontrackUser = ontrackUser;
    }

    @SuppressWarnings("unused")
    public void setOntrackPassword(String ontrackPassword) {
        this.ontrackPassword = ontrackPassword;
    }

    public int getOntrackMaxTries() {
        return ontrackMaxTries;
    }

    @SuppressWarnings("unused")
    public void setOntrackMaxTries(int ontrackMaxTries) {
        this.ontrackMaxTries = ontrackMaxTries;
    }

    public int getOntrackRetryDelaySeconds() {
        return ontrackRetryDelaySeconds;
    }

    @SuppressWarnings("unused")
    public void setOntrackRetryDelaySeconds(int ontrackRetryDelaySeconds) {
        this.ontrackRetryDelaySeconds = ontrackRetryDelaySeconds;
    }

    @SuppressWarnings("unused")
    public ListBoxModel doFillSecurityModeItems() {
        ListBoxModel items = new ListBoxModel();
        for (OntrackSecurityMode mode : OntrackSecurityMode.values()) {
            items.add(mode.getDisplayName(), mode.name());
        }
        return items;
    }
}
