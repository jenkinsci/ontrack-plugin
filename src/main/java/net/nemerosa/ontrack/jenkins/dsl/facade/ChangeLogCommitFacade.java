package net.nemerosa.ontrack.jenkins.dsl.facade;

public interface ChangeLogCommitFacade {

    String getId();
    String getShortId();
    String getAuthor();
    String getAuthorEmail();
    String getTimestamp();
    String getMessage();
    String getFormattedMessage();
    String getLink();

}