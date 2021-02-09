package net.nemerosa.ontrack.jenkins.dsl.v4;

import net.nemerosa.ontrack.dsl.v4.Ontrack;
import net.nemerosa.ontrack.dsl.v4.OntrackConnection;
import net.nemerosa.ontrack.dsl.v4.http.OTMessageClientException;
import net.nemerosa.ontrack.dsl.v4.http.OTNotFoundException;
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

public class OntrackDSLV4Facade implements OntrackDSLFacade {

    private final Ontrack ontrack;

    public OntrackDSLV4Facade(OntrackConfiguration config, OntrackDSLLogger logger) {
        OntrackConnection connection = OntrackConnection.create(config.getOntrackUrl());
        // Logging
        if (logger != null) {
            connection = connection.logger(logger::log);
        }
        // Authentication
        // TODO Ontrack V4 supports bearer tokens
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
        return OntrackConfiguration.VERSION_4;
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
        return new ProjectV4Facade(ontrack.project(project));
    }

    @Override
    public BranchFacade branch(String project, String branch) {
        return new BranchV4Facade(ontrack.branch(project, branch));
    }

    @Override
    public BuildFacade build(String projectName, String branchName, String buildName) {
        return new BuildV4Facade(ontrack.build(projectName, branchName, buildName));
    }
}
