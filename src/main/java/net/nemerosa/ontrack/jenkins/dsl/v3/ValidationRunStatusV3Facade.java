package net.nemerosa.ontrack.jenkins.dsl.v3;

import net.nemerosa.ontrack.dsl.ValidationRunStatus;
import net.nemerosa.ontrack.jenkins.dsl.facade.ValidationRunStatusFacade;

public class ValidationRunStatusV3Facade implements ValidationRunStatusFacade {

    private final ValidationRunStatus validationRunStatus;

    public ValidationRunStatusV3Facade(ValidationRunStatus validationRunStatus) {
        this.validationRunStatus = validationRunStatus;
    }

    @Override
    public void setDescription(String description) {
        validationRunStatus.setDescription(description);
    }
}
