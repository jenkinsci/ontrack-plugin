package net.nemerosa.ontrack.jenkins.dsl.v4;

import net.nemerosa.ontrack.dsl.v4.Build;
import net.nemerosa.ontrack.dsl.v4.TestSummary;
import net.nemerosa.ontrack.jenkins.dsl.facade.BuildFacade;
import net.nemerosa.ontrack.jenkins.dsl.facade.ChangeLogFacade;
import net.nemerosa.ontrack.jenkins.dsl.facade.ValidationRunFacade;

import java.util.Map;

public class BuildV4Facade implements BuildFacade {

    private final Build build;

    public BuildV4Facade(Build build) {
        this.build = build;
    }

    @Override
    public int getId() {
        return build.getId();
    }

    @Override
    public String getName() {
        return build.getName();
    }

    @Override
    public void promote(String promotionLevelName) {
        build.promote(promotionLevelName);
    }

    @Override
    public ValidationRunFacade validate(String validationStampName, String runStatus) {
        return new ValidationRunV4Facade(
                build.validate(validationStampName, runStatus)
        );
    }

    @Override
    public void setRunInfo(Map<String, ?> runInfo) {
        build.setRunInfo(runInfo);
    }

    @Override
    public void setJenkinsBuild(String ontrackConfigurationName, String projectPath, int number) {
        build.getConfig().jenkinsBuild(ontrackConfigurationName, projectPath, number);
    }

    @Override
    public void setGitCommit(String gitCommit) {
        build.getConfig().gitCommit(gitCommit);
    }

    @Override
    public ChangeLogFacade getChangeLog(BuildFacade build) {
        Build internalBuild = ((BuildV4Facade) build).build;
        return new ChangeLogV4Facade(
                this.build.getChangeLog(internalBuild)
        );
    }

    @Override
    public ValidationRunFacade validateWithFraction(String validationStamp, int numerator, int denominator, String status) {
        return new ValidationRunV4Facade(
                build.validateWithFraction(validationStamp, numerator, denominator, status)
        );
    }

    @Override
    public ValidationRunFacade validateWithTestSummary(String validationStamp, int passed, int skipped, int failed, String status) {
        return new ValidationRunV4Facade(
                build.validateWithTestSummary(
                        validationStamp,
                        new TestSummary(passed, skipped, failed),
                        status
                )
        );
    }

    @Override
    public ValidationRunFacade validateWithCHML(String validationStamp, int critical, int high, int medium, int low, String status) {
        return new ValidationRunV4Facade(
                build.validateWithCHML(
                        validationStamp, critical, high, medium, low, status)
        );
    }

    @Override
    public ValidationRunFacade validateWithText(String validationStamp, String status, String text) {
        return new ValidationRunV4Facade(
                build.validateWithText(validationStamp, status, text)
        );
    }

    @Override
    public ValidationRunFacade validateWithNumber(String validationStamp, int value, String status) {
        return new ValidationRunV4Facade(
                build.validateWithNumber(
                        validationStamp, value, status)
        );
    }

    @Override
    public ValidationRunFacade validateWithPercentage(String validationStamp, int value, String status) {
        return new ValidationRunV4Facade(
                build.validateWithPercentage(
                        validationStamp, value, status)
        );
    }

    @Override
    public ValidationRunFacade validateWithMetrics(String validationStamp, Map<String, Double> metrics, String status) {
        return new ValidationRunV4Facade(
                build.validateWithMetrics(
                        validationStamp, metrics, status)
        );
    }

    @Override
    public ValidationRunFacade validateWithData(String validationStamp, Map<String, ?> data, String dataType, String status) {
        return new ValidationRunV4Facade(
                build.validateWithData(
                        validationStamp,
                        data,
                        dataType,
                        status
                )
        );
    }
}
