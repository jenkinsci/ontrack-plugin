package net.nemerosa.ontrack.jenkins.dsl.facade;

import java.util.List;
import java.util.Map;

public interface BranchFacade {

    /**
     * Gets a list of builds for this branch using a filter
     *
     * @param filter Filter to use
     * @return List of builds
     */
    List<BuildFacade> standardFilter(Map<String, ?> filter);

    /**
     * Gets the list of last promoted builds for this branch
     *
     * @return List of builds
     */
    List<BuildFacade> getLastPromotedBuilds();

    /**
     * Gets an interval filter
     *
     * @param filter Interval filter
     * @return List of builds
     */
    List<BuildFacade> intervalFilter(Map<String, ?> filter);

    /**
     * Gets or creates a build for this branch
     *
     * @param name        Name of the build
     * @param description Description of the build
     * @param getIfExists If <code>true</code>, gets the build if it exists already, otherwise fails.
     * @return An existing build or new one
     */
    BuildFacade build(String name, String description, boolean getIfExists);
}