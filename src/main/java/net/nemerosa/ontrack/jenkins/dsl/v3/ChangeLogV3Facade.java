package net.nemerosa.ontrack.jenkins.dsl.v3;

import net.nemerosa.ontrack.dsl.ChangeLog;
import net.nemerosa.ontrack.jenkins.dsl.facade.*;

import java.util.List;
import java.util.stream.Collectors;

public class ChangeLogV3Facade implements ChangeLogFacade {
    private final ChangeLog changeLog;

    public ChangeLogV3Facade(ChangeLog changeLog) {
        this.changeLog = changeLog;
    }

    @Override
    public BuildFacade getFrom() {
        return new BuildV3Facade(changeLog.getFrom());
    }

    @Override
    public BuildFacade getTo() {
        return new BuildV3Facade(changeLog.getTo());
    }

    @Override
    public List<ChangeLogCommitFacade> getCommits() {
        return changeLog.getCommits().stream()
                .map(ChangeLogCommitV3Facade::new)
                .collect(Collectors.toList());
    }

    @Override
    public List<ChangeLogIssueFacade> getIssues() {
        return changeLog.getIssues().stream()
                .map(ChangeLogIssueV3Facade::new)
                .collect(Collectors.toList());
    }

    @Override
    public List<ChangeLogFileFacade> getFiles() {
        return changeLog.getFiles().stream()
                .map(ChangeLogFileV3Facade::new)
                .collect(Collectors.toList());
    }

    @Override
    public String getPageLink() {
        return changeLog.link("page");
    }
}
