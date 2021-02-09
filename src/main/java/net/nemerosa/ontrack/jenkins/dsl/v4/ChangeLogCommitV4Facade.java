package net.nemerosa.ontrack.jenkins.dsl.v4;

import net.nemerosa.ontrack.dsl.v4.ChangeLogCommit;
import net.nemerosa.ontrack.jenkins.dsl.facade.ChangeLogCommitFacade;

public class ChangeLogCommitV4Facade implements ChangeLogCommitFacade {

    private final ChangeLogCommit changeLogCommit;

    public ChangeLogCommitV4Facade(ChangeLogCommit changeLogCommit) {
        this.changeLogCommit = changeLogCommit;
    }

    @Override
    public String getId() {
        return changeLogCommit.getId();
    }

    @Override
    public String getShortId() {
        return changeLogCommit.getShortId();
    }

    @Override
    public String getAuthor() {
        return changeLogCommit.getAuthor();
    }

    @Override
    public String getAuthorEmail() {
        return changeLogCommit.getAuthorEmail();
    }

    @Override
    public String getTimestamp() {
        return changeLogCommit.getTimestamp();
    }

    @Override
    public String getMessage() {
        return changeLogCommit.getMessage();
    }

    @Override
    public String getFormattedMessage() {
        return changeLogCommit.getFormattedMessage();
    }

    @Override
    public String getLink() {
        return changeLogCommit.getLink();
    }
}
