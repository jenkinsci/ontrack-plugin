package net.nemerosa.ontrack.jenkins;

public interface MockBranch {
    MockBuild build(String project, String branch, boolean name);
}
