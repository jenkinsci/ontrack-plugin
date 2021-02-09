package net.nemerosa.ontrack.jenkins;

import java.util.Map;

public interface MockBuild {
    MockValidationRun validate(String vs, String status);

    MockValidationRun validateWithFraction(String stamp, int numerator, int denominator, String status);

    MockValidationRun validateWithMetrics(String stamp, Map<String, Double> metrics, String status);

    MockValidationRun validateWithCHML(String stamp, int critical, int high, int medium, int low, String status);

    MockValidationRun validateWithText(String stamp, String status, String text);

    MockValidationRun validateWithNumber(String stamp, int value, String status);

    MockValidationRun validateWithPercentage(String stamp, int value, String status);

    MockValidationRun validateWithData(String stamp, Object data, String dataType, String status);

    void promote(String promotionLevelName);

    void setRunInfo(Map<String, ?> runInfo);
}
