package net.nemerosa.ontrack.jenkins;

import com.fasterxml.jackson.databind.JsonNode;
import hudson.Extension;
import hudson.Launcher;
import hudson.model.*;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import net.nemerosa.ontrack.jenkins.support.client.OntrackClient;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.IOException;
import java.io.PrintStream;
import java.util.LinkedHashMap;
import java.util.Map;

import static net.nemerosa.ontrack.jenkins.support.client.OntrackClient.forBranch;

/**
 */
public class OntrackLastBuildWithPromotionLevel extends Builder {

    private final String project;
    private final String branch;
	private final String promotionLevel;
    private final String variable;

    @DataBoundConstructor
    public OntrackLastBuildWithPromotionLevel(String project, String branch, String variable, String promotionLevel) {
        this.project = project;
        this.branch = branch;
        this.variable = variable;
		this.promotionLevel = promotionLevel;
    }

    public String getProject() {
        return project;
    }

    public String getBranch() {
        return branch;
    }

    public String getVariable() {
        return variable;
    }

	public String getPromotionLevel() {
		return promotionLevel;
	}

    @Override
    public boolean perform(AbstractBuild<?, ?> theBuild, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {
        final String actualProject = OntrackPluginSupport.expand(project, theBuild, listener);
        final String actualBranch = OntrackPluginSupport.expand(branch, theBuild, listener);
        final String actualPromotionLevel = OntrackPluginSupport.expand(promotionLevel, theBuild, listener);

        JsonNode lastBuild = getBuild(System.out, actualProject, actualBranch, actualPromotionLevel);
        if (lastBuild != null) {
            String name = lastBuild.path("name").textValue();
            listener.getLogger().format("Found build '%s' for branch '%s' and project '%s' and promotion level '%s'%n", name, actualBranch, actualProject, actualPromotionLevel);
            theBuild.addAction(new ParametersAction(new StringParameterValue(variable, name)));
        } else {
            listener.getLogger().format("Could not find any build for branch '%s' and project '%s' and promotion level '%s'%n", actualBranch, actualProject, actualPromotionLevel);
            theBuild.setResult(Result.FAILURE);
        }
        return true;
    }

    /**
     * Get last build with a certain promotion level on a branch
     * @param logger The logger
     * @param project The project
     * @param branch The branch
     * @param promotionLevel The promotion level
     * @return The build or null of no build found.
     * @throws IOException
     */
    private JsonNode getBuild(PrintStream logger, final String project, final String branch, final String promotionLevel) throws IOException {
        Map<String, Object> filter = new LinkedHashMap<String, Object>();
        filter.put("count", 1);
        filter.put("withPromotionLevel", promotionLevel);
        JsonNode branchBuildView = OntrackClient.forBranch(logger, project, branch)
                .on("_view", "/net.nemerosa.ontrack.service.StandardBuildFilterProvider", filter)
                .get();
        JsonNode buildViews = branchBuildView.get("buildViews");
        if (buildViews.size() > 0) {
            // Gets the first build that complies
            return buildViews.get(0).get("build");
        } else {
            return null;
        }
    }

    @Extension
    public static class OntrackLastBuildStepDescription extends BuildStepDescriptor<Builder> {


        @Override
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return "Ontrack: Last build with promotion level";
        }
    }
}
