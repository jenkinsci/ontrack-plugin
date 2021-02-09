package net.nemerosa.ontrack.jenkins.dsl.v4;

import net.nemerosa.ontrack.dsl.v4.Branch;
import net.nemerosa.ontrack.jenkins.dsl.facade.BranchFacade;
import net.nemerosa.ontrack.jenkins.dsl.facade.BuildFacade;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BranchV4Facade implements BranchFacade {
    private final Branch branch;

    public BranchV4Facade(Branch branch) {
        this.branch = branch;
    }

    @Override
    public List<BuildFacade> standardFilter(Map<String, ?> filter) {
        return branch.standardFilter(filter).stream()
                .map(BuildV4Facade::new)
                .collect(Collectors.toList());
    }

    @Override
    public List<BuildFacade> getLastPromotedBuilds() {
        return branch.getLastPromotedBuilds().stream()
                .map(BuildV4Facade::new)
                .collect(Collectors.toList());
    }

    @Override
    public List<BuildFacade> intervalFilter(Map<String, ?> filter) {
        return branch.intervalFilter(filter).stream()
                .map(BuildV4Facade::new)
                .collect(Collectors.toList());
    }

    @Override
    public BuildFacade build(String name, String description, boolean getIfExists) {
        return new BuildV4Facade(
                branch.build(name, description, getIfExists)
        );
    }
}
