package net.nemerosa.ontrack.jenkins;

import hudson.model.ParameterValue;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class OntrackSingleParameterDefinitionTest {

    @Test
    public void dsl_returning_null_gives_an_empty_parameter() {
        OntrackSingleParameterDefinition definition = new OntrackSingleParameterDefinition(
                "NAME",
                "My parameter",
                "",
                true,
                "name",
                new MockDSLRunner(null)
        );
        ParameterValue parameterValue = definition.createValue();
        assertEquals("NAME", parameterValue.getName());
        assertEquals("", parameterValue.getValue());
    }

}
