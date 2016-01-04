package net.nemerosa.ontrack.jenkins;

import hudson.Util;
import org.kohsuke.stapler.DataBoundConstructor;

public class ScriptLocation {

    private final boolean usingText;
    private final String scriptPath;
    private final String scriptText;

    @DataBoundConstructor
    public ScriptLocation(String value, String scriptPath, String scriptText) {
        this.usingText = value == null || Boolean.parseBoolean(value);
        this.scriptPath = Util.fixEmptyAndTrim(scriptPath);
        this.scriptText = Util.fixEmptyAndTrim(scriptText);
    }

    public boolean isUsingText() {
        return usingText;
    }

    public String getScriptPath() {
        return scriptPath;
    }

    public String getScriptText() {
        return scriptText;
    }

    public static ScriptLocation text(String text) {
        return new ScriptLocation("true", null, text);
    }

    public static ScriptLocation path(String path) {
        return new ScriptLocation("false", path, null);
    }
}
