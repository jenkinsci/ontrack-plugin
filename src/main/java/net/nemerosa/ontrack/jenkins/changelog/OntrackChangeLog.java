package net.nemerosa.ontrack.jenkins.changelog;

import java.util.List;

public class OntrackChangeLog {

    private final String from;
    private final String to;
    private final List<OntrackChangeLogCommit> commits;
    private final List<OntrackChangeLogIssue> issues;
    private final List<OntrackChangeLogFile> files;

    public OntrackChangeLog(String from, String to,
                            List<OntrackChangeLogCommit> commits,
                            List<OntrackChangeLogIssue> issues,
                            List<OntrackChangeLogFile> files) {
        this.from = from;
        this.to = to;
        this.commits = commits;
        this.issues = issues;
        this.files = files;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public List<OntrackChangeLogCommit> getCommits() {
        return commits;
    }

    public List<OntrackChangeLogIssue> getIssues() {
        return issues;
    }

    public List<OntrackChangeLogFile> getFiles() {
        return files;
    }
}
