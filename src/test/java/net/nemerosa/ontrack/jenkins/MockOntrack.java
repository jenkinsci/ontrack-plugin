package net.nemerosa.ontrack.jenkins;

import java.util.Map;

public interface MockOntrack {
    MockBuild build(String project, String branch, String name);

    @SuppressWarnings("UnusedReturnValue")
    Object graphQLQuery(String query, Map<String, Object> vars);

    MockBranch branch(String project, String name);
}
