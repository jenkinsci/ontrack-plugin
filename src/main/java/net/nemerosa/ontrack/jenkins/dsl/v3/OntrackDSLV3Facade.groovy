package net.nemerosa.ontrack.jenkins.dsl.v3

import net.nemerosa.ontrack.dsl.Ontrack
import net.nemerosa.ontrack.dsl.OntrackConnection
import net.nemerosa.ontrack.dsl.OntrackLogger
import net.nemerosa.ontrack.dsl.http.OTMessageClientException
import net.nemerosa.ontrack.jenkins.OntrackConfiguration
import net.nemerosa.ontrack.jenkins.dsl.OntrackDSLFacade
import net.nemerosa.ontrack.jenkins.dsl.OntrackDSLLogger
import net.nemerosa.ontrack.jenkins.dsl.facade.BuildFacade
import org.apache.commons.lang.StringUtils

import java.util.function.Consumer

class OntrackDSLV3Facade implements OntrackDSLFacade {

    private Ontrack ontrack

    OntrackDSLV3Facade(OntrackConfiguration config, OntrackDSLLogger logger) {
        OntrackConnection connection = OntrackConnection.create(config.ontrackUrl)
        // Logging
        if (logger != null) {
            connection = connection.logger(new OntrackLogger() {
                @Override
                void trace(String message) {
                    logger.log(message)
                }
            })
        }
        // Authentication
        String user = config.getOntrackUser()
        if (StringUtils.isNotBlank(user)) {
            connection = connection.authenticate(
                    user,
                    config.getOntrackPassword()
            )
        }
        // Retries
        if (config.getOntrackMaxTries() >= 1) {
            connection = connection
                    .maxTries(config.getOntrackMaxTries())
                    .retryDelaySeconds(config.getOntrackRetryDelaySeconds())
        }
        // Building the Ontrack root
        ontrack = connection.build()
    }

    @Override
    String getVersion() {
        return OntrackConfiguration.VERSION_3
    }

    @Override
    void onClientException(Exception exception, Consumer<String> handler) {
        if (exception instanceof OTMessageClientException) {
            handler.accept(exception.message)
        } else {
            throw exception
        }
    }

    @Override
    BuildFacade build(String projectName, String branchName, String buildName) {
        return new BuildV3Facade(ontrack.build(projectName, branchName, buildName))
    }
}
