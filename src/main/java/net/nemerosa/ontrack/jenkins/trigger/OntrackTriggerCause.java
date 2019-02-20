package net.nemerosa.ontrack.jenkins.trigger;

import hudson.model.Cause;

public class OntrackTriggerCause extends Cause {

    @Override
    public String getShortDescription() {
        return "Triggered by Ontrack.";
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof OntrackTriggerCause;
    }

}
