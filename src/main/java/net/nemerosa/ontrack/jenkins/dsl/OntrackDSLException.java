package net.nemerosa.ontrack.jenkins.dsl;

import org.jenkinsci.plugins.scriptsecurity.sandbox.RejectedAccessException;
import org.jenkinsci.plugins.scriptsecurity.scripts.UnapprovedUsageException;

public class OntrackDSLException extends RuntimeException {
    public OntrackDSLException(UnapprovedUsageException e) {
        super("Script has not been approved yet.", e);
    }

    public OntrackDSLException(String message, RejectedAccessException e) {
        super(message, e);
    }
}
