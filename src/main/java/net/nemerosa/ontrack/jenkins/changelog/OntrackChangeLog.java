package net.nemerosa.ontrack.jenkins.changelog;

import java.util.List;

public class OntrackChangeLog {

    private final List<OntrackChangeLogCommit> commits;
    private final List<OntrackChangeLogIssue> issues;

    public OntrackChangeLog(List<OntrackChangeLogCommit> commits, List<OntrackChangeLogIssue> issues) {
        this.commits = commits;
        this.issues = issues;
    }

    public List<OntrackChangeLogCommit> getCommits() {
        return commits;
    }

    public List<OntrackChangeLogIssue> getIssues() {
        return issues;
    }
}
