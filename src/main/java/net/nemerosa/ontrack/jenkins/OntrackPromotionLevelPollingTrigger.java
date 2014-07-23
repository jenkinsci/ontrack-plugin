package net.nemerosa.ontrack.jenkins;

import antlr.ANTLRException;
import com.fasterxml.jackson.databind.JsonNode;
import hudson.Extension;
import hudson.FilePath;
import hudson.model.AbstractProject;
import hudson.model.Action;
import hudson.model.Item;
import hudson.model.Node;
import net.nemerosa.ontrack.jenkins.support.client.OntrackClient;
import org.apache.commons.lang.StringUtils;
import org.jenkinsci.lib.xtrigger.AbstractTrigger;
import org.jenkinsci.lib.xtrigger.XTriggerDescriptor;
import org.jenkinsci.lib.xtrigger.XTriggerException;
import org.jenkinsci.lib.xtrigger.XTriggerLog;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.LinkedHashMap;
import java.util.Map;

public class OntrackPromotionLevelPollingTrigger extends AbstractTrigger {

    private final String project;
    private final String branch;
    private final String promotionLevel;

    @DataBoundConstructor
    public OntrackPromotionLevelPollingTrigger(String cronTabSpec, String triggerLabel, String project, String branch,
                                               String promotionLevel) throws ANTLRException {
        super(cronTabSpec, triggerLabel);
        this.project = project;
        this.branch = branch;
        this.promotionLevel = promotionLevel;
    }

    @Override
    protected File getLogFile() {
        return new File(job.getRootDir(), "ontrack-promotionlevel-polling-trigger.log");
    }

    @Override
    protected boolean requiresWorkspaceForPolling() {
        return false;
    }

    @Override
    protected String getName() {
        return "Ontrack: Poll promotion level";
    }

    @Override
    protected Action[] getScheduledActions(Node node, XTriggerLog xTriggerLog) {
        return new Action[0];
    }

    @Override
    protected boolean checkIfModified(Node node, XTriggerLog xTriggerLog) throws XTriggerException {
        if (checkConfigs(xTriggerLog)) return false;

        String actualProject = resolveEnvVars(project, (AbstractProject) job, node);
        String actualBranch = resolveEnvVars(branch, (AbstractProject) job, node);
        String actualPromotionLevel = resolveEnvVars(promotionLevel, (AbstractProject) job, node);

        FilePath lastBuildNameFile = new FilePath(node.getRootPath(), String.format("%s-promotionlevel-lastBuildNr", job.getName()));
        String lastBuildName = loadLastBuildNr(xTriggerLog, lastBuildNameFile);

        // Gets the last build with this promotion level
        JsonNode lastBuild = null;
        try {
            lastBuild = getBuild(System.out, actualProject, actualBranch, actualPromotionLevel);
        } catch (IOException e) {
            logException(xTriggerLog, e);
        }

        // Found
        if (lastBuild != null) {
            String buildName = lastBuild.path("name").textValue();
            xTriggerLog.info(String.format("Found build '%s' for branch '%s' and project '%s' and promotion level '%s'%n", buildName, actualBranch, actualProject, actualPromotionLevel));
            try {
                if (lastBuildName == null || lastBuildName.isEmpty() || !lastBuildName.equals(buildName)) {
                    saveLastBuildNr(buildName, xTriggerLog, lastBuildNameFile);
                    return true;
                }
            } catch (IOException e) {
                logException(xTriggerLog, e);
            } catch (InterruptedException e) {
                logException(xTriggerLog, e);
            }
        }

        return false;
    }

    @Override
    protected String getCause() {
        return String.format("New build found with promotion level %s", promotionLevel);
    }

    public String getProject() {
        return project;
    }

    public String getBranch() {
        return branch;
    }

    public String getPromotionLevel() {
        return promotionLevel;
    }

    private static void saveLastBuildNr(String lastBuildNr, XTriggerLog xTriggerLog, FilePath lastBuildNrFile) throws IOException, InterruptedException {
        lastBuildNrFile.write(lastBuildNr, "UTF-8");
        xTriggerLog.info(String.format("Wrote buildNr: %s", lastBuildNr));
    }

    private static String loadLastBuildNr(XTriggerLog xTriggerLog, FilePath lastBuildNrFile) {
        String lastBuildNr = null;

        try {
            if (lastBuildNrFile.exists()) {
                lastBuildNr = lastBuildNrFile.readToString();

                xTriggerLog.info(String.format("Loaded buildNr: %s", lastBuildNr));
            }
        } catch (IOException e) {
            logException(xTriggerLog, e);
        } catch (InterruptedException e) {
            logException(xTriggerLog, e);
        }

        return lastBuildNr;
    }

    private boolean checkConfigs(XTriggerLog xTriggerLog) {
        if (StringUtils.isEmpty(project)) {
            xTriggerLog.info("Ontrack: No project configured");
            return true;
        }

        if (StringUtils.isEmpty(branch)) {
            xTriggerLog.info("Ontrack: No branch configured");
            return true;
        }

        if (StringUtils.isEmpty(promotionLevel)) {
            xTriggerLog.info("Ontrack: No promotion level configured");
            return true;
        }
        return false;
    }

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

    private static void logException(XTriggerLog xTriggerLog, Exception e) {
        e.printStackTrace();
        xTriggerLog.error(e.getMessage());
        for (StackTraceElement se : e.getStackTrace()) {
            xTriggerLog.error(se.toString());
        }
    }

    @Extension
    public static class OntrackValidationStampPollingTriggerDescriptor extends XTriggerDescriptor {
        @Override
        public boolean isApplicable(Item item) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return "Ontrack: Poll promotion level";
        }
    }
}
