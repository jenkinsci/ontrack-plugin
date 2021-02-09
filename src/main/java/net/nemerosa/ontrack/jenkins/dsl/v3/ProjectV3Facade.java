package net.nemerosa.ontrack.jenkins.dsl.v3;

import net.nemerosa.ontrack.dsl.Project;
import net.nemerosa.ontrack.jenkins.dsl.facade.BranchFacade;
import net.nemerosa.ontrack.jenkins.dsl.facade.ProjectFacade;

public class ProjectV3Facade implements ProjectFacade {

    private final Project project;

    public ProjectV3Facade(Project project) {
        this.project = project;
    }

    @Override
    public Object getDSLRoot() {
        return project;
    }

    @Override
    public BranchFacade branch(String name, String description, boolean getIfExists) {
        return new BranchV3Facade(
                project.branch(name, description, getIfExists)
        );
    }
}
