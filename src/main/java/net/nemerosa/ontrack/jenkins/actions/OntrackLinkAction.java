package net.nemerosa.ontrack.jenkins.actions;

import hudson.model.Action;
import org.kohsuke.stapler.DataBoundConstructor;

import javax.annotation.CheckForNull;

public class OntrackLinkAction implements Action {

    private final String name;
    private final String url;

    @DataBoundConstructor
    public OntrackLinkAction(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    @CheckForNull
    @Override
    public String getIconFileName() {
        return "/plugin/ontrack/icons/ontrack.png";
    }

    @CheckForNull
    @Override
    public String getDisplayName() {
        return name;
    }

    @CheckForNull
    @Override
    public String getUrlName() {
        return url;
    }

}
