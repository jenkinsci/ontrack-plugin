package net.nemerosa.ontrack.jenkins.dsl.facade;

import java.util.List;

public interface ChangeLogFileFacade {

    String getPath();

    List<String> getChangeTypes();

}
