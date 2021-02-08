package net.nemerosa.ontrack.jenkins.dsl.v3

import net.nemerosa.ontrack.dsl.Build
import net.nemerosa.ontrack.jenkins.dsl.facade.BuildFacade
import net.nemerosa.ontrack.jenkins.dsl.facade.ValidationRunFacade

class BuildV3Facade implements BuildFacade {

    private final Build build

    BuildV3Facade(Build build) {
        this.build = build
    }

    @Override
    ValidationRunFacade validate(String validationStampName, String runStatus) {
        return new ValidationRunV3Facade(
                build.validate(validationStampName, runStatus)
        )
    }



}
