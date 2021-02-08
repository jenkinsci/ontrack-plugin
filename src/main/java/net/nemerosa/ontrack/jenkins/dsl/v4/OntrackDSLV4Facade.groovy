package net.nemerosa.ontrack.jenkins.dsl.v4

import net.nemerosa.ontrack.dsl.v4.Ontrack
import net.nemerosa.ontrack.dsl.v4.OntrackConnection
import net.nemerosa.ontrack.dsl.v4.OntrackLogger
import net.nemerosa.ontrack.dsl.v4.http.OTMessageClientException
import net.nemerosa.ontrack.jenkins.OntrackConfiguration
import net.nemerosa.ontrack.jenkins.dsl.OntrackDSLFacade
import net.nemerosa.ontrack.jenkins.dsl.OntrackDSLLogger
import org.apache.commons.lang.StringUtils

import java.util.function.Consumer

class OntrackDSLV4Facade implements OntrackDSLFacade {

    private Ontrack ontrack

    OntrackDSLV4Facade(OntrackConfiguration config, OntrackDSLLogger logger) {
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
        // TODO Ontrack V4 supports bearer tokens
        String user = config.ontrackUser
        if (StringUtils.isNotBlank(user)) {
            connection = connection.authenticate(
                    user,
                    config.ontrackPassword
            )
        }
        // Retries
        if (config.ontrackMaxTries >= 1) {
            connection = connection
                    .maxTries(config.ontrackMaxTries)
                    .retryDelaySeconds(config.ontrackRetryDelaySeconds)
        }
        // Building the Ontrack root
        ontrack = connection.build()
    }

    @Override
    String getVersion() {
        return OntrackConfiguration.VERSION_4
    }

    @Override
    void onClientException(Exception exception, Consumer<String> handler) {
        if (exception instanceof OTMessageClientException) {
            handler.accept(exception.message)
        } else {
            throw exception
        }
    }

}
