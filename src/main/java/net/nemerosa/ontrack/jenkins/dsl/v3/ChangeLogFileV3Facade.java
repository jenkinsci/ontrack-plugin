package net.nemerosa.ontrack.jenkins.dsl.v3;

import net.nemerosa.ontrack.dsl.ChangeLogFile;
import net.nemerosa.ontrack.jenkins.dsl.facade.ChangeLogFileFacade;

import java.util.List;

public class ChangeLogFileV3Facade implements ChangeLogFileFacade {

    private final ChangeLogFile changeLogFile;

    public ChangeLogFileV3Facade(ChangeLogFile changeLogFile) {
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
