package net.nemerosa.ontrack.jenkins;

import java.util.Map;

public interface MockValidationRun {
    void setRunInfo(Map<String, ?> runInfo);

    MockValidationRunStatus getLastValidationRunStatus();
}
