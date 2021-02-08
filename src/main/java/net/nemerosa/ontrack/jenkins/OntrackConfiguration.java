package net.nemerosa.ontrack.jenkins;

import hudson.Extension;
import hudson.util.ListBoxModel;
import jenkins.management.AdministrativeMonitorsConfiguration;
import jenkins.model.GlobalConfiguration;
import jenkins.model.Jenkins;
import net.nemerosa.ontrack.dsl.OntrackConnection;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.StaplerRequest;

import javax.annotation.Nullable;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;

@Extension
public class OntrackConfiguration extends GlobalConfiguration {

    private static Logger LOGGER = Logger.getLogger(OntrackConfiguration.class.getName());

    public static final String VERSION_3 = "V3";
    public static final String VERSION_4 = "V4";

    public static @Nullable
    OntrackConfiguration getOntrackConfiguration() {
        Jenkins instance = Jenkins.getInstanceOrNull();
        return instance != null ? (OntrackConfiguration) instance.getDescriptor(OntrackConfiguration.class) : null;
    }

    private String ontrackConfigurationName;
    private String ontrackUrl;
    private String ontrackVersion;
    private String ontrackUser;
    private String ontrackPassword;
    private int ontrackMaxTries = 1;
    private int ontrackRetryDelaySeconds = 10000;
    private int ontrackVersionCacheExpirationSeconds = 3600;
    private OntrackSecurityMode securityMode = OntrackSecurityMode.DEFAULT;
    private boolean ontrackTraceTimings = false;

    private final transient AtomicReference<VersionCache> version = new AtomicReference<>();

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
        ontrackVersion = json.has("ontrackVersion") ? json.getString("ontrackVersion") : VERSION_3;
        ontrackUser = json.getString("ontrackUser");
        ontrackPassword = json.getString("ontrackPassword");
        ontrackMaxTries = json.getInt("ontrackMaxTries");
        ontrackRetryDelaySeconds = json.getInt("ontrackRetryDelaySeconds");
        ontrackVersionCacheExpirationSeconds = json.getInt("ontrackVersionCacheExpirationSeconds");
        securityMode = OntrackSecurityMode.valueOf(json.getString("securityMode"));
        ontrackTraceTimings = json.has("ontrackTraceTimings") && json.getBoolean("ontrackTraceTimings");
        save();
        boolean ok = super.configure(req, json);
        // Getting the version from Ontrack
        loadVersion();
        // OK
        return ok;
    }

    private VersionCache loadVersion() {
        return version.updateAndGet(current -> {
            if (current != null) {
                long expiredTimeMs = System.currentTimeMillis() - current.getTimestamp();
                long expiredTimeSeconds = expiredTimeMs / 1000;
                if (expiredTimeSeconds >= ontrackVersionCacheExpirationSeconds) {
                    return computeVersionCache();
                } else {
                    return current;
                }
            } else {
                return computeVersionCache();
            }
        });
    }

    private VersionCache computeVersionCache() {
        Version remoteVersion = getRemoteVersion();
        if (remoteVersion != null) {
            LOGGER.info("[ontrack] Remote version = " + remoteVersion);
            return new VersionCache(remoteVersion, System.currentTimeMillis());
        } else {
            return null;
        }
    }

    private Version getRemoteVersion() {
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
        VersionCache versionCache = loadVersion();
        return versionCache != null ? versionCache.getValue() : null;
    }

    public String getOntrackConfigurationName() {
        return ontrackConfigurationName;
    }

    public String getOntrackUrl() {
        return ontrackUrl;
    }

    public String getOntrackVersion() {
        return ontrackVersion;
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
    public void setOntrackVersion(String ontrackVersion) {
        this.ontrackVersion = ontrackVersion;
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
    public int getOntrackVersionCacheExpirationSeconds() {
        return ontrackVersionCacheExpirationSeconds;
    }

    @SuppressWarnings("unused")
    public void setOntrackVersionCacheExpirationSeconds(int ontrackVersionCacheExpirationSeconds) {
        this.ontrackVersionCacheExpirationSeconds = ontrackVersionCacheExpirationSeconds;
    }

    @SuppressWarnings("unused")
    public boolean isOntrackTraceTimings() {
        return ontrackTraceTimings;
    }

    @SuppressWarnings("unused")
    public void setOntrackTraceTimings(boolean ontrackTraceTimings) {
        this.ontrackTraceTimings = ontrackTraceTimings;
    }

    @SuppressWarnings("unused")
    public ListBoxModel doFillSecurityModeItems() {
        ListBoxModel items = new ListBoxModel();
        for (OntrackSecurityMode mode : OntrackSecurityMode.values()) {
            items.add(mode.getDisplayName(), mode.name());
        }
        return items;
    }

    private static class VersionCache {
        private final Version value;
        private final long timestamp;

        private VersionCache(Version value, long timestamp) {
            this.value = value;
            this.timestamp = timestamp;
        }

        public Version getValue() {
            return value;
        }

        long getTimestamp() {
            return timestamp;
        }
    }
}
