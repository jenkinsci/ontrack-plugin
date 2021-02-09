package net.nemerosa.ontrack.jenkins.dsl.v3;

import net.nemerosa.ontrack.dsl.ChangeLogIssue;
import net.nemerosa.ontrack.jenkins.dsl.facade.ChangeLogIssueFacade;

public class ChangeLogIssueV3Facade implements ChangeLogIssueFacade {

    private final ChangeLogIssue changeLogIssue;

    public ChangeLogIssueV3Facade(ChangeLogIssue changeLogIssue) {
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
