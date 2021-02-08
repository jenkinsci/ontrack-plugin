package net.nemerosa.ontrack.jenkins.dsl.facade

interface ChangeLogFileFacade {

    String getPath()

    List<String> getChangeTypes()

}
