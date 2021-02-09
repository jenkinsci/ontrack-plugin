package net.nemerosa.ontrack.jenkins.dsl.v4;

import net.nemerosa.ontrack.dsl.v4.Project;
import net.nemerosa.ontrack.jenkins.dsl.facade.BranchFacade;
import net.nemerosa.ontrack.jenkins.dsl.facade.ProjectFacade;

public class ProjectV4Facade implements ProjectFacade {

    private final Project project;

    public ProjectV4Facade(Project project) {
        this.project = project;
    }

    @Override
    public BranchFacade branch(String name, String description, boolean getIfExists) {
        return new BranchV4Facade(
                project.branch(name, description, getIfExists)
        );
    }
}
