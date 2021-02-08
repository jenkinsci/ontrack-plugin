package net.nemerosa.ontrack.jenkins;


import org.apache.commons.lang.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Version {

    private static final Pattern PATTERN = Pattern.compile("^([^.]+)(\\.([^.]+)(\\.([^.]+))?)?.*$");

    public static final Version NONE = new Version(0, 0, 0);

    private final int major;
    private final int minor;
    private final int patch;

    public Version(int major, int minor, int patch) {
        this.major = major;
        this.minor = minor;
        this.patch = patch;
    }

    @Override
    public String toString() {
        return String.format("%d.%d.%d", major, minor, patch);
    }

    public int getMajor() {
        return major;
    }

    public int getMinor() {
        return minor;
    }

    public int getPatch() {
        return patch;
    }

    @SuppressWarnings("unused")
    public boolean isValid() {
        return major >= 0 && minor >= 0 && patch >= 0 && (major > 0 || minor > 0 || patch > 0);
    }

    public static Version of(String value) {
        if (StringUtils.isNotBlank(value)) {
            Matcher matcher = PATTERN.matcher(value);
            if (matcher.matches()) {
                String majorValue = matcher.group(1);
                String minorValue = matcher.group(3);
                String patchValue = matcher.group(5);
                int major = parse(majorValue);
                int minor = parse(minorValue);
                int patch = parse(patchValue);
                return new Version(
                        major,
                        minor,
                        patch
                );
            } else {
                return Version.NONE;
            }
        } else {
            return Version.NONE;
        }
    }

    private static int parse(String value) {
        if (StringUtils.isBlank(value)) {
            return 0;
        } else if (StringUtils.isNumeric(value)) {
            return Integer.parseInt(value, 10);
        } else {
            return -1;
        }
    }

    public static Version of(int major, int minor, int patch) {
        return new Version(major, minor, patch);
    }
}
