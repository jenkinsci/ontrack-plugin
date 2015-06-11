package net.nemerosa.ontrack.jenkins.changelog;

import java.util.List;

public class OntrackChangeLogFile {

    private final String path;
    private final List<String> changeTypes;

    public OntrackChangeLogFile(String path, List<String> changeTypes) {
        this.path = path;
        this.changeTypes = changeTypes;
    }

    public String getPath() {
        return path;
    }

    public List<String> getChangeTypes() {
        return changeTypes;
    }

    public String getChangeType() {
        return changeTypes.get(changeTypes.size() - 1);
    }

}
