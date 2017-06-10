package net.nemerosa.ontrack.jenkins;

import hudson.FilePath;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.model.Run;
import hudson.model.TaskListener;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class OntrackPluginSupport {

    public static final String REGEX_ENV_VARIABLE = "\\$\\{([a-zA-Z0-9_]+)\\}";

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

    public static Map<String, String> parseProperties(String text, Run<?, ?> theBuild, TaskListener listener) throws IOException, InterruptedException {
        Map<String, String> properties = new LinkedHashMap<String, String>();
        String[] lines = StringUtils.split(text, "\n\r");
        for (String line : lines) {
            if (!StringUtils.isBlank(line)) {
                line = line.trim();
                if (!line.startsWith("#")) {
                    int pos = line.indexOf("=");
                    if (pos > 0) {
                        String name = line.substring(0, pos).trim();
                        String value = line.substring(pos + 1).trim();
                        String expandedValue = theBuild.getEnvironment(listener).expand(value);
                        properties.put(name, expandedValue);
                    }
                }
            }
        }
        return properties;
    }
}
