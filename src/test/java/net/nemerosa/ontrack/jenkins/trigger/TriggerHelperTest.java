package net.nemerosa.ontrack.jenkins.trigger;

import com.google.common.collect.ImmutableMap;
import hudson.model.ParameterValue;
import hudson.model.Result;
import net.nemerosa.ontrack.jenkins.dsl.OntrackDSLFacade;
import net.nemerosa.ontrack.jenkins.dsl.facade.BranchFacade;
import net.nemerosa.ontrack.jenkins.dsl.facade.BuildFacade;
import org.junit.Before;
import org.junit.Test;

import javax.annotation.CheckForNull;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TriggerHelperTest {

    private static final String PROJECT = "project";
    private static final String BRANCH = "branch";
    private static final String OTHER = "other";
    private static final String PROMOTION = "IRON";
    private static final String VERSION = "VERSION";
    private static final String OTHER_VERSION = "OTHER_VERSION";

    private static final String OLD_BUILD = "1.0.0";
    private static final String NEW_BUILD = "1.0.1";

    private static final String OLD_OTHER_BUILD = "2.0.0";
    private static final String NEW_OTHER_BUILD = "2.0.1";

    private OntrackDSLFacade ontrack;
    private BranchFacade ontrackBranch;
    private BranchFacade otherOntrackBranch;
    private BuildFacade ontrackBuild;
    private BuildFacade ontrackOtherBuild;

    @Before
    public void before() {
        ontrack = mock(OntrackDSLFacade.class);
        ontrackBranch = mock(BranchFacade.class);
        otherOntrackBranch = mock(BranchFacade.class);
        ontrackBuild = mock(BuildFacade.class);
        ontrackOtherBuild = mock(BuildFacade.class);

        when(ontrack.branch(PROJECT, BRANCH)).thenReturn(ontrackBranch);
        when(ontrack.branch(PROJECT, OTHER)).thenReturn(otherOntrackBranch);
    }

    /**
     * Single trigger with promotion.
     * No previous build.
     * No promotion.
     * <p>
     * => No triggered
     */
    @Test
    public void test1() {
        MockTriggerJob job = new MockTriggerJob().withNoPromotion();

        singleTrigger(job, PROMOTION);
        job.checkNotTriggered();
    }

    /**
     * Single trigger with promotion.
     * No previous build.
     * Promotion.
     * <p>
     * => Triggered with promoted build
     */
    @Test
    public void test2() {
        MockTriggerJob job = new MockTriggerJob().withPromotion(OLD_BUILD);

        singleTrigger(job, PROMOTION);

        job.checkTriggered(OLD_BUILD);
    }

    /**
     * Single trigger with promotion.
     * Previous build, without any parameter.
     * No promotion.
     * <p>
     * => No triggered
     */
    @Test
    public void test3() {
        MockTriggerJob job = new MockTriggerJob().withPreviousBuild().withNoPromotion();

        singleTrigger(job, PROMOTION);

        job.checkNotTriggered();
    }

    /**
     * Single trigger with promotion.
     * Previous build, without any parameter.
     * Promotion.
     * <p>
     * => Triggered with promoted build
     */
    @Test
    public void test4() {
        MockTriggerJob job = new MockTriggerJob().withPreviousBuild().withPromotion(OLD_BUILD);

        singleTrigger(job, PROMOTION);

        job.checkTriggered(OLD_BUILD);
    }

    /**
     * Single trigger with promotion.
     * Previous build, with same parameter.
     * Promotion.
     * <p>
     * => Not triggered.
     */
    @Test
    public void test5() {
        MockTriggerJob job = new MockTriggerJob().withPreviousBuild(OLD_BUILD).withPromotion(OLD_BUILD);

        singleTrigger(job, PROMOTION);

        job.checkNotTriggered();
    }

    /**
     * Single trigger with promotion.
     * Previous build, with old parameter.
     * Promotion.
     * <p>
     * => Not triggered.
     */
    @Test
    public void test6() {
        MockTriggerJob job = new MockTriggerJob().withPreviousBuild(OLD_BUILD).withPromotion(NEW_BUILD);

        singleTrigger(job, PROMOTION);

        job.checkTriggered(NEW_BUILD);
    }

    /**
     * Single trigger with promotion.
     * Previous build, with same parameter, but failed.
     * Promotion.
     * <p>
     * => Triggered with promoted build
     */
    @Test
    public void test7() {
        MockTriggerJob job = new MockTriggerJob().withPreviousBuild(Result.FAILURE, OLD_BUILD).withPromotion(OLD_BUILD);

        singleTrigger(job, PROMOTION);

        job.checkTriggered(OLD_BUILD);
    }

    /**
     * Single trigger without promotion.
     * No previous build.
     * No build.
     * <p>
     * => No triggered
     */
    @Test
    public void test10() {
        MockTriggerJob job = new MockTriggerJob().withNoBuild();

        singleTrigger(job);

        job.checkNotTriggered();
    }

    /**
     * Single trigger without promotion.
     * No previous build.
     * New build.
     * <p>
     * => Triggered with promoted build
     */
    @Test
    public void test11() {
        MockTriggerJob job = new MockTriggerJob().withBuild(OLD_BUILD);

        singleTrigger(job);

        job.checkTriggered(OLD_BUILD);
    }

    /**
     * Single trigger without promotion.
     * Previous build, without any parameter.
     * No build.
     * <p>
     * => No triggered
     */
    @Test
    public void test12() {
        MockTriggerJob job = new MockTriggerJob().withPreviousBuild().withNoBuild();

        singleTrigger(job);

        job.checkNotTriggered();
    }

    /**
     * Single trigger without promotion.
     * Previous build, without any parameter.
     * Build.
     * <p>
     * => Triggered with promoted build
     */
    @Test
    public void test13() {
        MockTriggerJob job = new MockTriggerJob().withPreviousBuild().withBuild(OLD_BUILD);

        singleTrigger(job);

        job.checkTriggered(OLD_BUILD);
    }

    /**
     * Single trigger without promotion.
     * Previous build, with same parameter.
     * Build.
     * <p>
     * => Not triggered.
     */
    @Test
    public void tes14() {
        MockTriggerJob job = new MockTriggerJob().withPreviousBuild(OLD_BUILD).withBuild(OLD_BUILD);

        singleTrigger(job);

        job.checkNotTriggered();
    }

    /**
     * Single trigger without promotion.
     * Previous build, with old parameter.
     * Build.
     * <p>
     * => Not triggered.
     */
    @Test
    public void test15() {
        MockTriggerJob job = new MockTriggerJob().withPreviousBuild(OLD_BUILD).withBuild(NEW_BUILD);

        singleTrigger(job);

        job.checkTriggered(NEW_BUILD);
    }

    /**
     * Single trigger without promotion.
     * Previous build, with same parameter, but failed.
     * Build.
     * <p>
     * => Triggered with promoted build
     */
    @Test
    public void test16() {
        MockTriggerJob job = new MockTriggerJob().withPreviousBuild(Result.FAILURE, OLD_BUILD).withBuild(OLD_BUILD);

        singleTrigger(job);

        job.checkTriggered(OLD_BUILD);
    }

    /**
     * Multiple triggers.
     * No previous build.
     * No match.
     * <p>
     * => No triggered
     */
    @Test
    public void test20() {
        MockTriggerJob job = new MockTriggerJob().withNoPromotion(ontrackBranch).withNoPromotion(otherOntrackBranch);
        multipleTrigger(job);
        job.checkNotTriggered();
    }

    /**
     * Multiple triggers.
     * No previous build.
     * One match.
     * <p>
     * => No triggered
     */
    @Test
    public void test21() {
        MockTriggerJob job = new MockTriggerJob().withNoPromotion(ontrackBranch).withPromotion(otherOntrackBranch, ontrackOtherBuild, OLD_OTHER_BUILD);
        multipleTrigger(job);
        job.checkNotTriggered();
    }

    /**
     * Multiple triggers.
     * No previous build.
     * All match.
     * <p>
     * => Triggered
     */
    @Test
    public void test22() {
        MockTriggerJob job = new MockTriggerJob().withPromotion(ontrackBranch, ontrackBuild, OLD_BUILD).withPromotion(otherOntrackBranch, ontrackOtherBuild, OLD_OTHER_BUILD);
        multipleTrigger(job);
        job.checkTriggered(VERSION, OLD_BUILD, OTHER_VERSION, OLD_OTHER_BUILD);
    }

    /**
     * Multiple triggers.
     * Previous build with no parameter.
     * No match.
     * <p>
     * => Not triggered
     */
    @Test
    public void test23() {
        MockTriggerJob job = new MockTriggerJob()
                .withPreviousBuild()
                .withNoPromotion(ontrackBranch).withNoPromotion(otherOntrackBranch);
        multipleTrigger(job);
        job.checkNotTriggered();
    }

    /**
     * Multiple triggers.
     * Previous build with no parameter.
     * One match.
     * <p>
     * => Not triggered
     */
    @Test
    public void test24() {
        MockTriggerJob job = new MockTriggerJob()
                .withPreviousBuild()
                .withNoPromotion(ontrackBranch).withPromotion(otherOntrackBranch, ontrackOtherBuild, OLD_OTHER_BUILD);
        multipleTrigger(job);
        job.checkNotTriggered();
    }

    /**
     * Multiple triggers.
     * Previous build with no parameter.
     * All match.
     * <p>
     * => Triggered
     */
    @Test
    public void test25() {
        MockTriggerJob job = new MockTriggerJob()
                .withPreviousBuild()
                .withPromotion(ontrackBranch, ontrackBuild, OLD_BUILD).withPromotion(otherOntrackBranch, ontrackOtherBuild, OLD_OTHER_BUILD);
        multipleTrigger(job);
        job.checkTriggered(VERSION, OLD_BUILD, OTHER_VERSION, OLD_OTHER_BUILD);
    }

    /**
     * Multiple triggers.
     * Previous build with one parameter, not matching
     * All match.
     * <p>
     * => Triggered
     */
    @Test
    public void test26() {
        MockTriggerJob job = new MockTriggerJob()
                .withPreviousBuild(VERSION, OLD_BUILD)
                .withPromotion(ontrackBranch, ontrackBuild, NEW_BUILD).withPromotion(otherOntrackBranch, ontrackOtherBuild, NEW_OTHER_BUILD);
        multipleTrigger(job);
        job.checkTriggered(VERSION, NEW_BUILD, OTHER_VERSION, NEW_OTHER_BUILD);
    }

    /**
     * Multiple triggers.
     * Previous build with one parameter, matching
     * All match.
     * <p>
     * => Triggered
     */
    @Test
    public void test27() {
        MockTriggerJob job = new MockTriggerJob()
                .withPreviousBuild(VERSION, NEW_BUILD)
                .withPromotion(ontrackBranch, ontrackBuild, NEW_BUILD).withPromotion(otherOntrackBranch, ontrackOtherBuild, NEW_OTHER_BUILD);
        multipleTrigger(job);
        job.checkTriggered(VERSION, NEW_BUILD, OTHER_VERSION, NEW_OTHER_BUILD);
    }

    /**
     * Multiple triggers.
     * Previous build with two parameter, one matching
     * All match.
     * <p>
     * => Triggered
     */
    @Test
    public void test28() {
        MockTriggerJob job = new MockTriggerJob()
                .withPreviousBuild(VERSION, NEW_BUILD, OTHER_VERSION, OLD_OTHER_BUILD)
                .withPromotion(ontrackBranch, ontrackBuild, NEW_BUILD).withPromotion(otherOntrackBranch, ontrackOtherBuild, NEW_OTHER_BUILD);
        multipleTrigger(job);
        job.checkTriggered(VERSION, NEW_BUILD, OTHER_VERSION, NEW_OTHER_BUILD);
    }

    /**
     * Multiple triggers.
     * Previous build with two parameter, all matching
     * All match.
     * <p>
     * => Not triggered
     */
    @Test
    public void test29() {
        MockTriggerJob job = new MockTriggerJob()
                .withPreviousBuild(VERSION, NEW_BUILD, OTHER_VERSION, NEW_OTHER_BUILD)
                .withPromotion(ontrackBranch, ontrackBuild, NEW_BUILD).withPromotion(otherOntrackBranch, ontrackOtherBuild, NEW_OTHER_BUILD);
        multipleTrigger(job);
        job.checkNotTriggered();
    }

    private void singleTrigger(MockTriggerJob job) {
        singleTrigger(job, null);
    }

    private void singleTrigger(MockTriggerJob job, String promotion) {
        TriggerHelper.evaluate(ontrack, job, Collections.singletonList(new TriggerDefinition(
                PROJECT,
                BRANCH,
                promotion,
                VERSION,
                null
        )));
    }

    private void multipleTrigger(MockTriggerJob job) {
        TriggerHelper.evaluate(ontrack, job,
                Arrays.asList(
                        new TriggerDefinition(
                                PROJECT,
                                BRANCH,
                                PROMOTION,
                                VERSION,
                                null
                        ),
                        new TriggerDefinition(
                                PROJECT,
                                OTHER,
                                PROMOTION,
                                OTHER_VERSION,
                                null
                        )
                )
        );
    }

    private class MockTriggerJob implements TriggerJob {

        private OntrackTriggerCause cause = null;
        private List<ParameterValue> parameters = null;

        private MockTriggerRun run = null;

        @Override
        public String getFullName() {
            return "TestJob";
        }

        @CheckForNull
        @Override
        public TriggerRun getLastBuild() {
            return run;
        }

        @Override
        public void trigger(OntrackTriggerCause cause, List<ParameterValue> parameters) {
            this.cause = cause;
            this.parameters = parameters;
        }

        public MockTriggerJob withNoPromotion() {
            return withNoPromotion(ontrackBranch);
        }

        public MockTriggerJob withNoPromotion(BranchFacade ontrackBranch) {
            when(ontrackBranch.standardFilter(ImmutableMap.of(
                    "count", 1,
                    "withPromotionLevel", PROMOTION
            ))).thenReturn(Collections.emptyList());
            return this;
        }

        public MockTriggerJob withNoBuild() {
            when(ontrackBranch.standardFilter(ImmutableMap.of(
                    "count", 1
            ))).thenReturn(Collections.emptyList());
            return this;
        }

        public MockTriggerJob withPromotion(String buildName) {
            return withPromotion(ontrackBranch, ontrackBuild, buildName);
        }

        public MockTriggerJob withPromotion(BranchFacade branch, BuildFacade build, String buildName) {
            when(build.getName()).thenReturn(buildName);
            when(branch.standardFilter(ImmutableMap.of(
                    "count", 1,
                    "withPromotionLevel", PROMOTION
            ))).thenReturn(Collections.singletonList(build));
            return this;
        }

        public MockTriggerJob withBuild(String buildName) {
            when(ontrackBuild.getName()).thenReturn(buildName);
            when(ontrackBranch.standardFilter(ImmutableMap.of(
                    "count", 1
            ))).thenReturn(Collections.singletonList(ontrackBuild));
            return this;
        }

        public MockTriggerJob withPreviousBuild() {
            return withPreviousBuild(
                    Result.SUCCESS,
                    Collections.emptyMap()
            );
        }

        public MockTriggerJob withPreviousBuild(String value) {
            return withPreviousBuild(
                    Result.SUCCESS,
                    value
            );
        }

        public MockTriggerJob withPreviousBuild(Result result, String value) {
            return withPreviousBuild(
                    result,
                    Collections.singletonMap(VERSION, value)
            );
        }

        public MockTriggerJob withPreviousBuild(String name, String value) {
            return withPreviousBuild(
                    Result.SUCCESS,
                    Collections.singletonMap(name, value)
            );
        }

        public MockTriggerJob withPreviousBuild(String name1, String value1, String name2, String value2) {
            return withPreviousBuild(
                    Result.SUCCESS,
                    ImmutableMap.of(name1, value1, name2, value2)
            );
        }

        public MockTriggerJob withPreviousBuild(Result result, Map<String, String> parameters) {
            run = new MockTriggerRun(result, parameters);
            return this;
        }

        public void checkNotTriggered() {
            assertNull(cause);
            assertNull(parameters);
        }

        public void checkTriggered(String value) {
            checkTriggered(VERSION, value);
        }

        public void checkTriggered(String name, String value) {
            checkTriggered(Collections.singletonMap(name, value));
        }

        public void checkTriggered(String name1, String value1, String name2, String value2) {
            checkTriggered(ImmutableMap.of(name1, value1, name2, value2));
        }

        public void checkTriggered(Map<String, String> expected) {
            assertNotNull("Should have been triggered but missing cause", cause);
            assertNotNull("Should have been triggered but missing parameters", parameters);
            assertEquals(expected.size(), parameters.size());
            parameters.forEach((parameterValue) -> {
                String name = parameterValue.getName();
                Object actualValue = parameterValue.getValue();
                if (actualValue instanceof String) {
                    String expectedValue = expected.get(name);
                    assertEquals(String.format("Expected string value %s for parameter %s but got %s", expectedValue, name, actualValue), expectedValue, actualValue);
                } else {
                    fail(String.format("Expected string value for parameter %s but got %s", name, actualValue));
                }
            });
        }
    }

    private class MockTriggerRun implements TriggerRun {

        private final Result result;
        private final Map<String, String> parameters;

        public MockTriggerRun(Result result, Map<String, String> parameters) {
            this.result = result;
            this.parameters = parameters;
        }

        @CheckForNull
        @Override
        public Result getResult() {
            return result;
        }

        @CheckForNull
        @Override
        public String getEnvironment(String name) {
            return parameters.get(name);
        }
    }

}
