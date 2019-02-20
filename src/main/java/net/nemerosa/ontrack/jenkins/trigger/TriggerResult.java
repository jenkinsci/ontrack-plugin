package net.nemerosa.ontrack.jenkins.trigger;

import org.apache.commons.lang.StringUtils;

import javax.annotation.Nullable;

/**
 * Result for the evaluation of a {@link TriggerDefinition}.
 */
public class TriggerResult {

    /**
     * Parameter name
     */
    private final String name;

    /**
     * Old parameter value (can be null)
     */
    private final String oldValue;

    /**
     * New parameter value (can be null)
     */
    private final String newValue;

    public TriggerResult(String name, @Nullable String oldValue, @Nullable String newValue) {
        this.name = name;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    public String getName() {
        return name;
    }

    @Nullable
    public String getOldValue() {
        return oldValue;
    }

    @Nullable
    public String getNewValue() {
        return newValue;
    }

    public boolean isFiring() {
        return StringUtils.isNotBlank(newValue) && !StringUtils.equals(oldValue, newValue);
    }
}
