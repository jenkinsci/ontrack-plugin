package net.nemerosa.ontrack.jenkins;

import hudson.Extension;
import jenkins.model.GlobalConfiguration;
import jenkins.model.Jenkins;
import net.nemerosa.ontrack.dsl.OntrackConnection;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.StaplerRequest;

import javax.annotation.Nullable;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.UnaryOperator;
import java.util.logging.Logger;

@Extension
public class OntrackConfiguration extends GlobalConfiguration {

    private static Logger LOGGER = Logger.getLogger(OntrackConfiguration.class.getName());

    public static OntrackConfiguration getOntrackConfiguration() {
        Jenkins instance = Jenkins.getInstance();
        return instance != null ? (OntrackConfiguration) instance.getDescriptor(OntrackConfiguration.class) : null;
    }

    private String ontrackConfigurationName;
    private String ontrackUrl;
    private String ontrackUser;
    private String ontrackPassword;
    private int ontrackMaxTries = 1;
    private int ontrackRetryDelaySeconds = 10000;
    private int ontrackVersionCacheExpirationSeconds = 3600;

    private final AtomicReference<VersionCache> version = new AtomicReference<>();

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

    public int getOntrackVersionCacheExpirationSeconds() {
        return ontrackVersionCacheExpirationSeconds;
    }

    public void setOntrackVersionCacheExpirationSeconds(int ontrackVersionCacheExpirationSeconds) {
        this.ontrackVersionCacheExpirationSeconds = ontrackVersionCacheExpirationSeconds;
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
