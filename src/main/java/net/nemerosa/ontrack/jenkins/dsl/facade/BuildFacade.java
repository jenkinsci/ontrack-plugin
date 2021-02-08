package net.nemerosa.ontrack.jenkins.dsl.facade;

import java.util.Map;

/**
 * DSL facade for a build
 */
public interface BuildFacade {

    /**
     * ID of the build
     */
    int getId();

    /**
     * Name of the build
     */
    String getName();

    /**
     * Creates a promotion run for this build
     *
     * @param promotionLevelName Name of the promotion level
     */
    void promote(String promotionLevelName);

    /**
     * Creates a validation run for this build
     *
     * @param validationStampName Name of the validation stamp
     * @param runStatus           Status for the run
     * @return Validation run facade
     */
    ValidationRunFacade validate(String validationStampName, String runStatus);

    /**
     * Sets some run info on this build.
     *
     * @param runInfo Run info to set
     */
    void setRunInfo(Map<String, ?> runInfo);

    /**
     * Sets the Jenkins property on the target build
     *
     * @param ontrackConfigurationName Name of the Jenkins configuration in Ontrack
     * @param projectPath              Path to the job in Jenkins
     * @param number                   Build number in Jenkins
     */
    void setJenkinsBuild(String ontrackConfigurationName, String projectPath, int number);

    /**
     * Sets the Git commit property on the target build
     *
     * @param gitCommit Git commit
     */
    void setGitCommit(String gitCommit);

    /**
     * Gets the change log from this build to the other one
     *
     * @param build Other bound
     * @return Change log
     */
    ChangeLogFacade getChangeLog(BuildFacade build);

    /**
     * Validates with a fraction as data
     *
     * @param validationStamp Name of the validation stamp
     * @param numerator       Fraction numerator
     * @param denominator     Fraction denominator
     * @param status          Optional status for the run
     * @return Validation run
     */
    ValidationRunFacade validateWithFraction(String validationStamp, int numerator, int denominator, String status);

    /**
     * Validates with a test summary as data
     *
     * @param validationStamp Name of the validation stamp
     * @param passed          Number of passed tests
     * @param skipped         Number of skipped tests
     * @param failed          Number of failed tests
     * @param status          Optional status for the run
     * @return Validation run
     */
    ValidationRunFacade validateWithTestSummary(String validationStamp, int passed, int skipped, int failed, String status);

    /**
     * Validates with CHML data
     *
     * @param validationStamp Name of the validation stamp
     * @param critical        Number of critical issues
     * @param high            Number of high issues
     * @param medium          Number of medium issues
     * @param low             Number of low issues
     * @param status          Optional status for the run
     * @return Validation run
     */
    ValidationRunFacade validateWithCHML(String validationStamp, int critical, int high, int medium, int low, String status);

    /**
     * Validates with text data
     *
     * @param validationStamp Name of the validation stamp
     * @param text            Text to set
     * @param status          Optional status for the run
     * @return Validation run
     */
    ValidationRunFacade validateWithText(String validationStamp, String status, String text);

    /**
     * Validates with numeric data
     *
     * @param validationStamp Name of the validation stamp
     * @param value           Value to set
     * @param status          Optional status for the run
     * @return Validation run
     */
    ValidationRunFacade validateWithNumber(String validationStamp, int value, String status);

    /**
     * Validates with percentage data
     *
     * @param validationStamp Name of the validation stamp
     * @param value           Value to set
     * @param status          Optional status for the run
     * @return Validation run
     */
    ValidationRunFacade validateWithPercentage(String validationStamp, int value, String status);

    /**
     * Validates with metrics data
     *
     * @param validationStamp Name of the validation stamp
     * @param metrics         Value to set
     * @param status          Optional status for the run
     * @return Validation run
     */
    ValidationRunFacade validateWithMetrics(String validationStamp, Map<String, Double> metrics, String status);

    /**
     * Validates with raw data
     *
     * @param validationStamp Name of the validation stamp
     * @param data            Raw data
     * @param dataType        Data type
     * @param status          Optional status for the run
     * @return Validation run
     */
    ValidationRunFacade validateWithData(String validationStamp, Map<String, ?> data, String dataType, String status);
}