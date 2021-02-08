package net.nemerosa.ontrack.jenkins.dsl.facade

interface ChangeLogFacade {

    BuildFacade getFrom()
    BuildFacade getTo()

    List<ChangeLogCommitFacade> getCommits()

    List<ChangeLogIssueFacade> getIssues()

    List<ChangeLogFileFacade> getFiles()

    String getPageLink()
}