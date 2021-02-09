package net.nemerosa.ontrack.jenkins.dsl.v4;

import net.nemerosa.ontrack.dsl.v4.ChangeLogIssue;
import net.nemerosa.ontrack.jenkins.dsl.facade.ChangeLogIssueFacade;

public class ChangeLogIssueV4Facade implements ChangeLogIssueFacade {

    private final ChangeLogIssue changeLogIssue;

    public ChangeLogIssueV4Facade(ChangeLogIssue changeLogIssue) {
        this.changeLogIssue = changeLogIssue;
    }

    @Override
    public String getKey() {
        return changeLogIssue.getKey();
    }

    @Override
    public String getDisplayKey() {
        return changeLogIssue.getDisplayKey();
    }

    @Override
    public String getSummary() {
        return changeLogIssue.getSummary();
    }

    @Override
    public String getUrl() {
        return changeLogIssue.getUrl();
    }

    @Override
    public String getStatus() {
        return changeLogIssue.getStatus();
    }

    @Override
    public String getUpdateTime() {
        return changeLogIssue.getUpdateTime();
    }
}
