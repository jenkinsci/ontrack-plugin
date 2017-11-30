package net.nemerosa.ontrack.jenkins;

public enum OntrackSecurityMode {
    DEFAULT("Default security model"),
    SANDBOX("Always use the sandbox"),
    NONE("Disable security checks");

    private final String displayName;

    OntrackSecurityMode(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
