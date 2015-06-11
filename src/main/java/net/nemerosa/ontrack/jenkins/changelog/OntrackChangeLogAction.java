package net.nemerosa.ontrack.jenkins.changelog;

import hudson.model.Action;
import org.kohsuke.stapler.DataBoundConstructor;

import java.util.List;

public class OntrackChangeLogAction implements Action {

    private final List<OntrackChangeLog> changeLogs;

    @DataBoundConstructor
    public OntrackChangeLogAction(List<OntrackChangeLog> changeLogs) {
        this.changeLogs = changeLogs;
    }

    @SuppressWarnings("unused")
    public List<OntrackChangeLog> getChangeLogs() {
        return changeLogs;
    }

    @Override
    public String getIconFileName() {
        return "/ontrack/changelog.png";
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
