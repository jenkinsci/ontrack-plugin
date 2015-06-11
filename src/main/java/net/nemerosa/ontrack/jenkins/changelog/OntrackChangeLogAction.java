package net.nemerosa.ontrack.jenkins.changelog;

import hudson.model.AbstractBuild;
import hudson.model.Action;
import org.kohsuke.stapler.DataBoundConstructor;

import java.util.List;

public class OntrackChangeLogAction implements Action {

    private final AbstractBuild<?, ?> build;
    private final List<OntrackChangeLog> changeLogs;

    @DataBoundConstructor
    public OntrackChangeLogAction(AbstractBuild<?, ?> build, List<OntrackChangeLog> changeLogs) {
        this.build = build;
        this.changeLogs = changeLogs;
    }

    @SuppressWarnings("unused")
    public List<OntrackChangeLog> getChangeLogs() {
        return changeLogs;
    }

    public AbstractBuild<?, ?> getBuild() {
        return build;
    }

    @Override
    public String getIconFileName() {
        return "/plugin/ontrack/icons/changelog.png";
    }

    @Override
    public String getDisplayName() {
        return "Ontrack Change Log";
    }

    @Override
    public String getUrlName() {
        return "ontrack-changelog";
    }
}
