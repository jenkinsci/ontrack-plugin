package net.nemerosa.ontrack.jenkins.dsl.v4;

import net.nemerosa.ontrack.dsl.v4.ChangeLog;
import net.nemerosa.ontrack.jenkins.dsl.facade.*;

import java.util.List;
import java.util.stream.Collectors;

public class ChangeLogV4Facade implements ChangeLogFacade {

    private final ChangeLog changeLog;

    public ChangeLogV4Facade(ChangeLog changeLog) {
        this.changeLog = changeLog;
    }

    @Override
    public BuildFacade getFrom() {
        return new BuildV4Facade(changeLog.getFrom());
    }

    @Override
    public BuildFacade getTo() {
        return new BuildV4Facade(changeLog.getTo());
    }

    @Override
    public List<ChangeLogCommitFacade> getCommits() {
        return changeLog.getCommits().stream()
                .map(ChangeLogCommitV4Facade::new)
                .collect(Collectors.toList());
    }

    @Override
    public List<ChangeLogIssueFacade> getIssues() {
        return changeLog.getIssues().stream()
                .map(ChangeLogIssueV4Facade::new)
                .collect(Collectors.toList());
    }

    @Override
    public List<ChangeLogFileFacade> getFiles() {
        return changeLog.getFiles().stream()
                .map(ChangeLogFileV4Facade::new)
                .collect(Collectors.toList());
    }

    @Override
    public String getPageLink() {
        return changeLog.link("page");
    }
}
