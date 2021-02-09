package net.nemerosa.ontrack.jenkins.dsl.v3;

import net.nemerosa.ontrack.dsl.Ontrack;
import net.nemerosa.ontrack.dsl.OntrackConnection;
import net.nemerosa.ontrack.dsl.http.OTMessageClientException;
import net.nemerosa.ontrack.dsl.http.OTNotFoundException;
import net.nemerosa.ontrack.jenkins.OntrackConfiguration;
import net.nemerosa.ontrack.jenkins.dsl.OntrackDSLFacade;
import net.nemerosa.ontrack.jenkins.dsl.OntrackDSLLogger;
import net.nemerosa.ontrack.jenkins.dsl.facade.BranchFacade;
import net.nemerosa.ontrack.jenkins.dsl.facade.BuildFacade;
import net.nemerosa.ontrack.jenkins.dsl.facade.ProjectFacade;
import org.apache.commons.lang.StringUtils;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class OntrackDSLV3Facade implements OntrackDSLFacade {

    private final Ontrack ontrack;

    public OntrackDSLV3Facade(OntrackConfiguration config, OntrackDSLLogger logger) {
        OntrackConnection connection = OntrackConnection.create(config.getOntrackUrl());
        // Logging
        if (logger != null) {
            connection = connection.logger(logger::log);
        }
        // Authentication
        String user = config.getOntrackUser();
        if (StringUtils.isNotBlank(user)) {
            connection = connection.authenticate(
                    user,
                    config.getOntrackPassword()
            );
        }
        // Retries
        if (config.getOntrackMaxTries() >= 1) {
            connection = connection
                    .maxTries(config.getOntrackMaxTries())
                    .retryDelaySeconds(config.getOntrackRetryDelaySeconds());
        }
        // Building the Ontrack root
        ontrack = connection.build();
    }

    @Override
    public String getVersion() {
        return OntrackConfiguration.VERSION_3;
    }

    @Override
    public Object getDSLRoot() {
        return ontrack;
    }

    @Override
    public void onClientException(Exception exception, Consumer<String> handler) {
        if (exception instanceof OTMessageClientException) {
            handler.accept(exception.getMessage());
        } else {
            throw new RuntimeException("Client", exception);
        }
    }

    @Override
    public <T> T onNotFoundException(Exception exception, Supplier<T> handler) {
        if (exception instanceof OTNotFoundException) {
            return handler.get();
        } else {
            throw new RuntimeException("Not found", exception);
        }
    }

    @Override
    public Object graphQLQuery(String query, Map<String, ?> vars) {
        return ontrack.graphQLQuery(query, vars);
    }

    @Override
    public ProjectFacade project(String project) {
        return new ProjectV3Facade(ontrack.project(project));
    }

    @Override
    public BranchFacade branch(String project, String branch) {
        return new BranchV3Facade(ontrack.branch(project, branch));
    }

    @Override
    public BuildFacade build(String projectName, String branchName, String buildName) {
        return new BuildV3Facade(ontrack.build(projectName, branchName, buildName));
    }
}
