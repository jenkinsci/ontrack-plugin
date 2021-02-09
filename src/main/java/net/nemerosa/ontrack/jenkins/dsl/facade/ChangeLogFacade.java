package net.nemerosa.ontrack.jenkins.dsl.facade;

import java.util.List;

public interface ChangeLogFacade {

    BuildFacade getFrom();

    BuildFacade getTo();

    List<ChangeLogCommitFacade> getCommits();

    List<ChangeLogIssueFacade> getIssues();

    List<ChangeLogFileFacade> getFiles();

    String getPageLink();
}