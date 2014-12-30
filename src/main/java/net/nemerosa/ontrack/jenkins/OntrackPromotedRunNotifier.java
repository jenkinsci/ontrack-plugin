package net.nemerosa.ontrack.jenkins;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Publisher;
import net.nemerosa.ontrack.dsl.Build;
import net.nemerosa.ontrack.dsl.Ontrack;
import net.nemerosa.ontrack.jenkins.dsl.OntrackDSLConnector;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.IOException;

import static net.nemerosa.ontrack.jenkins.OntrackPluginSupport.expand;

/**
 * Allows to notify for a promoted run.
 */
public class OntrackPromotedRunNotifier extends AbstractOntrackNotifier {

    private final String project;
    private final String branch;
    private final String build;
    private final String promotionLevel;

    @DataBoundConstructor
    public OntrackPromotedRunNotifier(String project, String branch, String build, String promotionLevel) {
        this.project = project;
        this.branch = branch;
        this.build = build;
        this.promotionLevel = promotionLevel;
    }

    public String getProject() {
        return project;
    }

    public String getBranch() {
        return branch;
    }

    public String getBuild() {
        return build;
    }

    public String getPromotionLevel() {
        return promotionLevel;
    }

    @Override
    public boolean perform(AbstractBuild<?, ?> theBuild, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {
        // Expands the expressions into actual values
        final String projectName = expand(project, theBuild, listener);
        final String branchName = expand(branch, theBuild, listener);
        final String buildName = expand(build, theBuild, listener);
        final String promotionLevelName = expand(promotionLevel, theBuild, listener);
        // Only triggers in case of success
        if (theBuild.getResult().isBetterOrEqualTo(Result.SUCCESS)) {
            // Gets the Ontrack connector
            Ontrack ontrack = OntrackDSLConnector.createOntrackConnector(listener);
            // Gets the build
            Build build = ontrack.build(projectName, branchName, buildName);
            // Promotes it
            listener.getLogger().format("[ontrack] Promoting build %s of branch %s of project %s for %s%n", buildName, branchName, projectName, promotionLevelName);
            build.promote(promotionLevelName);
        } else {
            listener.getLogger().format("[ontrack] No promotion to %s since build is broken", promotionLevelName);
        }
        // OK
        return true;
    }

    @Extension
    public static final class OntrackPromotedRunDescriptorImpl extends BuildStepDescriptor<Publisher> {

        public OntrackPromotedRunDescriptorImpl() {
            super(OntrackPromotedRunNotifier.class);
        }

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return "Ontrack: Promoted run creation";
        }
    }
}
