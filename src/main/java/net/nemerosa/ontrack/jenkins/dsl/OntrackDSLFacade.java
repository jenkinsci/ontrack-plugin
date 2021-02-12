package net.nemerosa.ontrack.jenkins.dsl;


import net.nemerosa.ontrack.jenkins.dsl.facade.BranchFacade;
import net.nemerosa.ontrack.jenkins.dsl.facade.BuildFacade;
import net.nemerosa.ontrack.jenkins.dsl.facade.ProjectFacade;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

public interface OntrackDSLFacade {

    /**
     * Version of this facade
     *
     * @return Version being supported by this facade
     * @see net.nemerosa.ontrack.jenkins.OntrackConfiguration#getOntrackVersion()
     * @see net.nemerosa.ontrack.jenkins.OntrackConfiguration#VERSION_3
     * @see net.nemerosa.ontrack.jenkins.OntrackConfiguration#VERSION_4
     */
    String getVersion();

    /**
     * Gets the root object to inject as <code>ontrack</code> into the
     * DSL scripts.
     *
     * @return Object to inject in the Ontrack DSL scripts
     */
    Object getDSLRoot();

    /**
     * Catching client exceptions
     *
     * @param exception Exception to manage
     * @param handler   Handler called with the exception message if the exception is a client exception
     */
    void onClientException(Exception exception, Consumer<String> handler);

    /**
     * Catching not found exceptions
     *
     * @param <T>       Type of object returned by the handler
     * @param exception Exception to manage
     * @param handler   Handler called if the exception is a "not found" exception
     * @return Value returned by the handler in case of "not found" exception
     */
    <T> T onNotFoundException(Exception exception, Supplier<T> handler);

    /**
     * Gets a build
     *
     * @param projectName Name of the project
     * @param branchName  Name of the branch
     * @param buildName   Name of the build
     * @return Existing build
     */
    BuildFacade build(String projectName, String branchName, String buildName);

    /**
     * Gets a branch
     *
     * @param project Name of the project
     * @param branch  Name of the branch
     * @return Existing branch
     */
    BranchFacade branch(String project, String branch);

    /**
     * Gets a project
     *
     * @param project Name of the project
     * @return Existing project
     */
    ProjectFacade project(String project);

    /**
     * Runs an arbitrary GraphQL query
     *
     * @param query GraphQL query
     * @param vars  Variables for the query
     * @return GraphQL JSON response
     */
    Object graphQLQuery(String query, Map<String, ?> vars);

    /**
     * Gets the version of the remote app version
     *
     * @return Version
     */
    String getAppVersion();
}