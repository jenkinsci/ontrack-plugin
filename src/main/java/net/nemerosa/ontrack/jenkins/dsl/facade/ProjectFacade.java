package net.nemerosa.ontrack.jenkins.dsl.facade;

public interface ProjectFacade {
    /**
     * Gets or creates a branch for this project
     *
     * @param name        Name of the branch
     * @param description Description of the branch
     * @param getIfExists If <code>true</code>, gets the branch if it exists already, otherwise fails.
     * @return Created or existing branch
     */
    BranchFacade branch(String name, String description, boolean getIfExists);

    /**
     * Object to inject in any Ontrack DSL script
     * @return Object to inject in place of this facade
     */
    Object getDSLRoot();
}
