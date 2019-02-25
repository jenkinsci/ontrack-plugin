package net.nemerosa.ontrack.jenkins.trigger;

import com.google.common.collect.ImmutableMap;
import hudson.model.ParameterValue;
import hudson.model.Result;
import net.nemerosa.ontrack.dsl.Branch;
import net.nemerosa.ontrack.dsl.Build;
import net.nemerosa.ontrack.dsl.Ontrack;
import org.junit.Before;
import org.junit.Test;

import javax.annotation.CheckForNull;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TriggerHelperTest {

    private static final String PROJECT = "project";
    private static final String BRANCH = "branch";
    private static final String PROMOTION = "IRON";
    private static final String VERSION = "VERSION";

    private static final String OLD_BUILD = "1.0.0";
    private static final String NEW_BUILD = "1.0.1";

    private Ontrack ontrack;
    private Branch ontrackBranch;
    private Build ontrackBuild;

    @Before
    public void before() {
        ontrack = mock(Ontrack.class);
        ontrackBranch = mock(Branch.class);
        ontrackBuild = mock(Build.class);

        when(ontrack.branch(PROJECT, BRANCH)).thenReturn(ontrackBranch);
    }

    /**
     * No previous build.
     * No promotion.
     *
     * => No triggered
     */
    @Test
    public void test1() {
        MockTriggerJob job = new MockTriggerJob().withNoPromotion();

        TriggerHelper.evaluate(ontrack, job, Collections.singletonList(new TriggerDefinition(
                PROJECT,
                BRANCH,
                PROMOTION,
                VERSION,
                null
        )));
        job.checkNotTriggered();
    }

    /**
     * No previous build.
     * Promotion.
     *
     * => Triggered with promoted build
     */
    @Test
    public void test2() {
        MockTriggerJob job = new MockTriggerJob().withPromotion(OLD_BUILD);

        TriggerHelper.evaluate(ontrack, job, Collections.singletonList(new TriggerDefinition(
                PROJECT,
                BRANCH,
                PROMOTION,
                VERSION,
                null
        )));

        job.checkTriggered(Collections.singletonMap(VERSION, OLD_BUILD));
    }

    /**
     * Previous build, without any parameter.
     * No promotion.
     *
     * => No triggered
     */
    @Test
    public void test3() {
        MockTriggerJob job = new MockTriggerJob().withPreviousBuild().withNoPromotion();

        TriggerHelper.evaluate(ontrack, job, Collections.singletonList(new TriggerDefinition(
                PROJECT,
                BRANCH,
                PROMOTION,
                VERSION,
                null
        )));

        job.checkNotTriggered();
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
            // FIXME Method net.nemerosa.ontrack.jenkins.trigger.TriggerHelperTest.MockTriggerJob.getLastBuild
            return null;
        }

        @Override
        public void trigger(OntrackTriggerCause cause, List<ParameterValue> parameters) {
            this.cause = cause;
            this.parameters = parameters;
        }

        public MockTriggerJob withNoPromotion() {
            when(ontrackBranch.standardFilter(ImmutableMap.of(
                    "count", 1,
                    "withPromotionLevel", PROMOTION
            ))).thenReturn(Collections.emptyList());
            return this;
        }

        public MockTriggerJob withPromotion(String buildName) {
            when(ontrackBuild.getName()).thenReturn(buildName);
            when(ontrackBranch.standardFilter(ImmutableMap.of(
                    "count", 1,
                    "withPromotionLevel", PROMOTION
            ))).thenReturn(Collections.singletonList(ontrackBuild));
            return this;
        }

        public MockTriggerJob withPreviousBuild() {
            return withPreviousBuild(
                    Result.SUCCESS,
                    Collections.emptyMap()
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

        public void checkTriggered(Map<String, String> expected) {
            assertNotNull(cause);
            assertNotNull(parameters);
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
