package net.nemerosa.ontrack.jenkins.changelog;

import java.util.Collections;
import java.util.List;

public class OntrackChangeLog {

    private final boolean error;
    private final String from;
    private final String to;
    private final String pageLink;
    private final List<OntrackChangeLogCommit> commits;
    private final List<OntrackChangeLogIssue> issues;
    private final List<OntrackChangeLogFile> files;

    public OntrackChangeLog(boolean error, String from, String to,
                            String pageLink,
                            List<OntrackChangeLogCommit> commits,
                            List<OntrackChangeLogIssue> issues,
                            List<OntrackChangeLogFile> files) {
        this.error = error;
        this.from = from;
        this.to = to;
        this.pageLink = pageLink;
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

    public String getPageLink() {
        return pageLink;
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

    public static OntrackChangeLog error(String from, String to) {
        return new OntrackChangeLog(
                true,
                from, to,
                null,
                Collections.<OntrackChangeLogCommit>emptyList(),
                Collections.<OntrackChangeLogIssue>emptyList(),
                Collections.<OntrackChangeLogFile>emptyList()
        );
    }
}
