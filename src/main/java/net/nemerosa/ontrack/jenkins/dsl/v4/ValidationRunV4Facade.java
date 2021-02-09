package net.nemerosa.ontrack.jenkins.dsl.v4;

import net.nemerosa.ontrack.dsl.v4.ValidationRun;
import net.nemerosa.ontrack.jenkins.dsl.facade.ValidationRunFacade;
import net.nemerosa.ontrack.jenkins.dsl.facade.ValidationRunStatusFacade;

import java.util.Map;

public class ValidationRunV4Facade implements ValidationRunFacade {

    private final ValidationRun validationRun;

    public ValidationRunV4Facade(ValidationRun validationRun) {
        this.validationRun = validationRun;
    }

    @Override
    public void setRunInfo(Map<String, ?> runInfo) {
        validationRun.setRunInfo(runInfo);
    }

    @Override
    public ValidationRunStatusFacade getLastValidationRunStatus() {
        return new ValidationRunStatusV4Facade(
                validationRun.getLastValidationRunStatus()
        );
    }
}
