package net.nemerosa.ontrack.jenkins;

import net.nemerosa.ontrack.jenkins.dsl.DSLRunner;

public class MockDSLRunner implements DSLRunner {

    private final Object value;

    public MockDSLRunner(Object value) {
        this.value = value;
    }

    @Override
    public Object run(String dsl) {
        return value;
    }
}
