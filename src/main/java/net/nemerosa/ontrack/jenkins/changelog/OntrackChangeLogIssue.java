package net.nemerosa.ontrack.jenkins.changelog;

public class OntrackChangeLogIssue {

    private final String key;
    private final String displayKey;
    private final String summary;
    private final String status;
    private final String updateTime;
    private final String url;

    public OntrackChangeLogIssue(String key, String displayKey, String summary, String status, String updateTime, String url) {
        this.key = key;
        this.displayKey = displayKey;
        this.summary = summary;
        this.status = status;
        this.updateTime = updateTime;
        this.url = url;
    }

    public String getKey() {
        return key;
    }

    public String getDisplayKey() {
        return displayKey;
    }

    public String getSummary() {
        return summary;
    }

    public String getStatus() {
        return status;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public String getUrl() {
        return url;
    }
}
