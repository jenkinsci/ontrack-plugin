package net.nemerosa.ontrack.jenkins.dsl.v3

import com.google.common.collect.ImmutableMap
import net.nemerosa.ontrack.dsl.Branch
import net.nemerosa.ontrack.jenkins.dsl.facade.BranchFacade
import net.nemerosa.ontrack.jenkins.dsl.facade.BuildFacade

class BranchV3Facade implements BranchFacade {

    private final Branch branch

    BranchV3Facade(Branch branch) {
        this.branch = branch
    }

    @Override
    List<BuildFacade> intervalFilter(ImmutableMap<String, ?> filter) {
        return branch.intervalFilter(filter).collect {
            new BuildV3Facade(it)
        }
    }

}
