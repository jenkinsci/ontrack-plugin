package net.nemerosa.ontrack.jenkins.trigger;

import org.junit.Test;

import static org.junit.Assert.*;

public class TriggerDefinitionTest {

    private final TriggerDefinition trigger = new TriggerDefinition("project", "branch", "promotion", "name", null);

    @Test
    public void defaultMinimumResult() {
        assertEquals("SUCCESS", trigger.getMinimumResult());
    }

    @Test
    public void noResult() {
        TriggerResult result = trigger.noResult();
        assertEquals("name", result.getName());
        assertNull(result.getOldValue());
        assertNull(result.getNewValue());
        assertFalse(result.isFiring());
    }

    @Test
    public void noPrevious() {
        TriggerResult result = trigger.noPrevious("new");
        assertEquals("name", result.getName());
        assertNull(result.getOldValue());
        assertEquals("new", result.getNewValue());
        assertTrue(result.isFiring());
    }

    @Test
    public void withSamePrevious() {
        TriggerResult result = trigger.withPrevious("old", "old");
        assertEquals("name", result.getName());
        assertEquals("old", result.getOldValue());
        assertEquals("old", result.getNewValue());
        assertFalse(result.isFiring());
    }

    @Test
    public void withPrevious() {
        TriggerResult result = trigger.withPrevious("old", "new");
        assertEquals("name", result.getName());
        assertEquals("old", result.getOldValue());
        assertEquals("new", result.getNewValue());
        assertTrue(result.isFiring());
    }

}