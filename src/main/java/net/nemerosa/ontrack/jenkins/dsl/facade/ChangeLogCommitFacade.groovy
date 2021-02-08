package net.nemerosa.ontrack.jenkins.dsl.facade

interface ChangeLogCommitFacade {

    String getId()
    String getShortId()
    String getAuthor()
    String getAuthorEmail()
    String getTimestamp()
    String getMessage()
    String getFormattedMessage()
    String getLink()

}