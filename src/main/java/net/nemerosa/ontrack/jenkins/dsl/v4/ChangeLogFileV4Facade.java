package net.nemerosa.ontrack.jenkins.dsl.v4;

import net.nemerosa.ontrack.dsl.v4.ChangeLogFile;
import net.nemerosa.ontrack.jenkins.dsl.facade.ChangeLogFileFacade;

import java.util.List;

public class ChangeLogFileV4Facade implements ChangeLogFileFacade {

    private final ChangeLogFile changeLogFile;

    public ChangeLogFileV4Facade(ChangeLogFile changeLogFile) {
        this.changeLogFile = changeLogFile;
    }

    @Override
    public String getPath() {
        return changeLogFile.getPath();
    }

    @Override
    public List<String> getChangeTypes() {
        return changeLogFile.getChangeTypes();
    }
}
