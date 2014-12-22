package net.nemerosa.ontrack.jenkins.support.dsl;

public class OntrackDSLResult {

    private final Object shellResult;
    private final JenkinsConnector connector;

    public OntrackDSLResult(Object shellResult, JenkinsConnector connector) {
        this.shellResult = shellResult;
        this.connector = connector;
    }

    public Object getShellResult() {
        return shellResult;
    }

    public JenkinsConnector getConnector() {
        return connector;
    }

}
