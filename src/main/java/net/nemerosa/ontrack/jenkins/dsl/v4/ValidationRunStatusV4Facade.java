package net.nemerosa.ontrack.jenkins.dsl.v4;

import net.nemerosa.ontrack.dsl.v4.ValidationRunStatus;
import net.nemerosa.ontrack.jenkins.dsl.facade.ValidationRunStatusFacade;

public class ValidationRunStatusV4Facade implements ValidationRunStatusFacade {
    private final ValidationRunStatus validationRunStatus;

    public ValidationRunStatusV4Facade(ValidationRunStatus validationRunStatus) {
        this.validationRunStatus = validationRunStatus;
    }

    @Override
    public void setDescription(String description) {
        validationRunStatus.setDescription(description);
    }
}
