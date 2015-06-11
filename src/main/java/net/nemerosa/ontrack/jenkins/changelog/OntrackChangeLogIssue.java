package net.nemerosa.ontrack.jenkins.changelog;

public class OntrackChangeLogIssue {

    private final String key;
    private final String displayKey;
    private final String summary;
    private final String status;
    private final String updateTime;

    public OntrackChangeLogIssue(String key, String displayKey, String summary, String status, String updateTime) {
        this.key = key;
        this.displayKey = displayKey;
        this.summary = summary;
        this.status = status;
        this.updateTime = updateTime;
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
}
