package net.nemerosa.ontrack.jenkins;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import hudson.EnvVars;
import hudson.FilePath;
import hudson.model.*;
import hudson.triggers.SCMTrigger;
import jenkins.model.Jenkins;
import net.nemerosa.ontrack.dsl.Build;
import net.nemerosa.ontrack.dsl.Ontrack;
import net.nemerosa.ontrack.jenkins.actions.OntrackLinkAction;
import org.apache.commons.lang.StringUtils;

import javax.annotation.CheckForNull;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class OntrackPluginSupport {

    public static final String REGEX_ENV_VARIABLE = "\\$\\{([a-zA-Z0-9_]+)}";

    public static String readScript(AbstractBuild build, boolean usingText, String scriptText, String scriptPath) throws IOException {
        if (usingText) {
            return scriptText;
        } else {
            FilePath workspace = build.getWorkspace();
            if (workspace != null) {
                FilePath path = workspace.child(scriptPath);
                try {
                    return path.readToString();
                } catch (InterruptedException e) {
                    throw new IOException("Cannot read from " + scriptPath, e);
                }
            } else {
                throw new IOException("Cannot get a workspace to get the script path at " + scriptPath);
            }
        }
    }

    public static String expand(String template, AbstractBuild<?, ?> theBuild, BuildListener listener) {
        if (StringUtils.isBlank(template)) {
            return template;
        } else {
            Pattern pattern = Pattern.compile(REGEX_ENV_VARIABLE);
            Matcher matcher = pattern.matcher(template);
            StringBuffer result = new StringBuffer();
            while (matcher.find()) {
                String name = matcher.group(1);
                String value = getParameter(name, theBuild, listener);
                if (value == null) {
                    throw new IllegalStateException("Cannot find any replacement value for environment variable " + name);
                }
                matcher = matcher.appendReplacement(result, value);
            }
            matcher.appendTail(result);
            return result.toString();
        }
    }

    public static String getParameter(String name, AbstractBuild<?, ?> theBuild, BuildListener listener) {
        String value = theBuild.getBuildVariableResolver().resolve(name);
        if (value != null) {
            return value;
        } else {
            try {
                return theBuild.getEnvironment(listener).get(name);
            } catch (Exception ex) {
                throw new RuntimeException("Cannot get value for " + name, ex);
            }
        }
    }

    public static Map<String, String> parseProperties(String text, @CheckForNull Run<?, ?> theBuild, @CheckForNull TaskListener listener) throws IOException, InterruptedException {
        Map<String, String> properties = new LinkedHashMap<>();
        String[] lines = StringUtils.split(text, "\n\r");
        for (String line : lines) {
            if (!StringUtils.isBlank(line)) {
                line = line.trim();
                if (!line.startsWith("#")) {
                    int pos = line.indexOf("=");
                    if (pos > 0) {
                        String name = line.substring(0, pos).trim();
                        String value = line.substring(pos + 1).trim();
                        String expandedValue;
                        if (theBuild != null && listener != null) {
                            expandedValue = theBuild.getEnvironment(listener).expand(value);
                        } else {
                            expandedValue = value;
                        }
                        properties.put(name, expandedValue);
                    }
                }
            }
        }
        return properties;
    }

    public static Map<String, Object> getRunInfo(Run theBuild, TaskListener taskListener) throws IOException, InterruptedException {
        // Checks the version of Ontrack
        OntrackConfiguration configuration = OntrackConfiguration.getOntrackConfiguration();
        if (configuration != null) {
            Version version = configuration.getVersion();
            if (version != null && version.isValid()) {
                int major = version.getMajor();
                int minor = version.getMinor();
                boolean versionOk = (major == 2 && minor >= 35) || (major == 3 && minor >= 35) || (major > 3);
                if (!versionOk) {
                    return Collections.emptyMap();
                }
            }
        }
        // Gets the URL of this build
        Jenkins jenkins = Jenkins.getInstanceOrNull();
        if (jenkins == null) {
            return Collections.emptyMap();
        }
        String url = jenkins.getRootUrl() + theBuild.getUrl();
        // Gets the cause of this build
        String triggerType = null;
        String triggerData = null;
        @SuppressWarnings("unchecked")
        List<Cause> causes = theBuild.getCauses();
        if (!causes.isEmpty()) {
            Cause cause = causes.get(0);
            if (cause instanceof SCMTrigger.SCMTriggerCause) {
                triggerType = "scm";
                EnvVars environment = theBuild.getEnvironment(taskListener);
                String git_commit = environment.get("GIT_COMMIT");
                if (StringUtils.isNotBlank(git_commit)) {
                    triggerData = git_commit;
                } else {
                    String svn_revision = environment.get("SVN_REVISION");
                    if (StringUtils.isNotBlank(svn_revision)) {
                        triggerData = svn_revision;
                    } else {
                        triggerData = "n/a";
                    }
                }
            } else if (cause instanceof Cause.UserIdCause) {
                triggerType = "user";
                triggerData = ((Cause.UserIdCause) cause).getUserId();
            }
        }
        // Gets the duration of this build
        long durationMs = theBuild.getDuration();
        long durationSeconds;
        if (durationMs > 0) {
            durationSeconds = durationMs / 1000;
        } else {
            durationSeconds = (System.currentTimeMillis() - theBuild.getStartTimeInMillis()) / 1000;
        }
        // Creates the run info
        Map<String, Object> runInfo = new HashMap<>();
        runInfo.put("sourceType", "jenkins");
        runInfo.put("sourceUri", url);
        if (triggerType != null && triggerData != null) {
            runInfo.put("triggerType", triggerType);
            runInfo.put("triggerData", triggerData);
        }
        if (durationSeconds > 0) {
            runInfo.put("runTime", durationSeconds);
        }
        // OK
        return runInfo;
    }

    private static void addOntrackLink(String name, String suffix, Run run) {
        OntrackConfiguration ontrackConfiguration = OntrackConfiguration.getOntrackConfiguration();
        if (ontrackConfiguration != null) {
            String baseUrl = ontrackConfiguration.getOntrackUrl();
            String url = baseUrl + suffix;
            run.addAction(new OntrackLinkAction(name, url));
        }
    }

    public static void createOntrackLinks(Ontrack ontrack, Run run, Build ontrackBuild) {
        // Creates the build link
        addOntrackLink("Ontrack Build", "/#/build/" + ontrackBuild.getId(), run);
        // Gets build information
        Map<String, Integer> vars = Collections.singletonMap("id", ontrackBuild.getId());
        Object jsonResult = ontrack.graphQLQuery("query BuildInfo($id: Int!) { builds(id: $id) { branch { id project { id } } } }", vars);
        JsonNode json = new ObjectMapper().valueToTree(jsonResult);
        if (json != null) {
            // Gets branch and project id
            JsonNode branchJson = json.path("data").path("builds").path(0).path("branch");
            int branchId = branchJson.path("id").asInt();
            int projectId = branchJson.path("project").path("id").asInt();
            // Links
            addOntrackLink("Ontrack Branch", "/#/branch/" + branchId, run);
            addOntrackLink("Ontrack Project", "/#/project/" + projectId, run);
        }
    }
}
