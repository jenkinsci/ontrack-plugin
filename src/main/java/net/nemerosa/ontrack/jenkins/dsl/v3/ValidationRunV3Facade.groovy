package net.nemerosa.ontrack.jenkins.dsl.v3

import net.nemerosa.ontrack.dsl.ValidationRun
import net.nemerosa.ontrack.jenkins.dsl.facade.ValidationRunFacade

class ValidationRunV3Facade implements ValidationRunFacade {

    private final ValidationRun validationRun

    ValidationRunV3Facade(ValidationRun validationRun) {
        this.validationRun = validationRun
    }

    @Override
    void setRunInfo(Map<String, Object> runInfo) {
        validationRun.setRunInfo(runInfo)
    }
}
