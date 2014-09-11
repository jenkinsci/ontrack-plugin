package net.nemerosa.ontrack.jenkins;

import com.fasterxml.jackson.databind.JsonNode;
import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Publisher;
import net.nemerosa.ontrack.jenkins.support.client.OntrackResource;
import net.nemerosa.ontrack.jenkins.support.json.JsonUtils;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.IOException;

import static net.nemerosa.ontrack.jenkins.OntrackPluginSupport.expand;
import static net.nemerosa.ontrack.jenkins.support.client.OntrackClient.forBuild;

/**
 * Allows to notify for a build.
 */
public class OntrackBuildPropertySetter extends AbstractOntrackNotifier {

    private final String project;
    private final String branch;
    private final String build;
    private final String typeName;
    private final String value;

    @DataBoundConstructor
    public OntrackBuildPropertySetter(String project, String branch, String build, String typeName, String value) {
        this.project = project;
        this.branch = branch;
        this.build = build;
        this.typeName = typeName;
        this.value = value;
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

    public String getTypeName() {
        return typeName;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean perform(final AbstractBuild<?, ?> theBuild, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {
        // Only triggers in case of success
        if (theBuild.getResult().isBetterOrEqualTo(Result.SUCCESS)) {
            // Expands the expressions into actual values
            final String projectName = expand(project, theBuild, listener);
            final String branchName = expand(branch, theBuild, listener);
            final String buildName = expand(build, theBuild, listener);
            final String typeName = expand(this.typeName, theBuild, listener);
            final String theValue = expand(value, theBuild, listener);
            // Logging of parameters
            listener.getLogger().format(
                    "Setting property %s = %s on build %s for branch %s of project %s%n",
                    typeName, theValue,
                    buildName, branchName, projectName);
            // For the build
            JsonNode buildProperties = forBuild(listener.getLogger(), projectName, branchName, buildName).on("_properties").get();
            // Looking for property
            JsonNode propertyDef = null;
            for (JsonNode propertyDefCandidate : buildProperties.path("resources")) {
                String candidateTypeName = propertyDefCandidate.path("typeDescriptor").path("typeName").asText();
                if (typeName.equals(candidateTypeName)) {
                    propertyDef = propertyDefCandidate;
                }
            }
            // No property found
            if (propertyDef == null) {
                listener.getLogger().format("Could not find property %s for build %s.%n", typeName, buildName);
                theBuild.setResult(Result.FAILURE);
            }
            // Checks if the property is editable
            else if (!propertyDef.path("editable").asBoolean()) {
                listener.getLogger().format("Property %s on build %s is not editable.%n", typeName, buildName);
                theBuild.setResult(Result.FAILURE);
            }
            // Setting the property
            else {
                new OntrackResource(listener.getLogger(), propertyDef).on("_update").post(
                        // FIXME Need to be a custom JSON, or a list of key/values
                        JsonUtils.object()
                                .with("name", theValue)
                                .end()
                );
            }
        }
        // OK
        return true;
    }

    @Extension
    public static final class OntrackBuildPropertySetterDescriptorImpl extends BuildStepDescriptor<Publisher> {

        public OntrackBuildPropertySetterDescriptorImpl() {
            super(OntrackBuildPropertySetter.class);
        }

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return "Ontrack: Build property setter";
        }
    }
}
