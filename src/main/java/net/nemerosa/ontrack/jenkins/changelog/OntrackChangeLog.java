package net.nemerosa.ontrack.jenkins.changelog;

import java.util.List;

public class OntrackChangeLog {

    private final List<OntrackChangeLogCommit> commits;

    public OntrackChangeLog(List<OntrackChangeLogCommit> commits) {
        this.commits = commits;
    }

    public List<OntrackChangeLogCommit> getCommits() {
        return commits;
    }
}
