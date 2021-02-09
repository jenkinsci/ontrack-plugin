package net.nemerosa.ontrack.jenkins.steps;

import com.google.common.collect.ImmutableMap;
import net.nemerosa.ontrack.jenkins.MockBuild;
import net.nemerosa.ontrack.jenkins.MockOntrack;
import net.nemerosa.ontrack.jenkins.MockValidationRun;
import net.nemerosa.ontrack.jenkins.MockValidationRunStatus;
import net.nemerosa.ontrack.jenkins.dsl.OntrackDSLConnector;
import net.nemerosa.ontrack.jenkins.dsl.OntrackDSLFacade;
import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.mockito.Mockito.mock;

public class OntrackValidateStepRunTest {

    @Rule
    public JenkinsRule jenkinsRule = new JenkinsRule();

    @Test
    public void test_validate_default() throws Exception {
        WorkflowJob job = jenkinsRule.jenkins.createProject(WorkflowJob.class, "workflow");
        // leave out the subject
        job.setDefinition(new CpsFlowDefinition("ontrackValidate(project: 'prj', branch: 'master', build: '1', validationStamp: 'VS')", true));

        OntrackDSLFacade ontrackFacade = mock(OntrackDSLFacade.class);
        MockOntrack ontrack = mock(MockOntrack.class);
        when(ontrackFacade.getDSLRoot()).thenReturn(ontrack);
        MockBuild mockBuild = mock(MockBuild.class);
        MockValidationRun mockRun = mock(MockValidationRun.class);

        when(ontrack.build("prj", "master", "1")).thenReturn(mockBuild);
        when(mockBuild.validate("VS", "PASSED")).thenReturn(mockRun);

        OntrackDSLConnector.setOntrack(ontrackFacade);

        jenkinsRule.assertBuildStatusSuccess(job.scheduleBuild2(0));

        verify(mockBuild, times(1)).validate("VS", "PASSED");
        verify(mockRun, times(1)).setRunInfo(anyMapOf(String.class, Object.class));
    }

    @Test
    public void test_validate_and_set_comment() throws Exception {
        WorkflowJob job = jenkinsRule.jenkins.createProject(WorkflowJob.class, "workflow");
        // leave out the subject
        job.setDefinition(new CpsFlowDefinition("ontrackValidate(project: 'prj', branch: 'master', build: '1', validationStamp: 'VS', description: 'Some description')", true));

        OntrackDSLFacade ontrackFacade = mock(OntrackDSLFacade.class);
        MockOntrack ontrack = mock(MockOntrack.class);
        when(ontrackFacade.getDSLRoot()).thenReturn(ontrack);
        MockBuild mockBuild = mock(MockBuild.class);
        MockValidationRun mockRun = mock(MockValidationRun.class);
        MockValidationRunStatus mockRunStatus = mock(MockValidationRunStatus.class);

        when(ontrack.build("prj", "master", "1")).thenReturn(mockBuild);
        when(mockBuild.validate("VS", "PASSED")).thenReturn(mockRun);
        when(mockRun.getLastValidationRunStatus()).thenReturn(mockRunStatus);

        OntrackDSLConnector.setOntrack(ontrackFacade);

        jenkinsRule.assertBuildStatusSuccess(job.scheduleBuild2(0));

        verify(mockBuild, times(1)).validate("VS", "PASSED");
        verify(mockRun, times(1)).setRunInfo(anyMapOf(String.class, Object.class));
        verify(mockRunStatus, times(1)).setDescription("Some description");
    }

    @Test
    public void test_validate_custom() throws Exception {
        WorkflowJob job = jenkinsRule.jenkins.createProject(WorkflowJob.class, "workflow");
        // leave out the subject
        job.setDefinition(new CpsFlowDefinition("ontrackValidate(project: 'prj', branch: 'master', build: '1', validationStamp: 'VS', validationStatus: 'FAILED')", true));

        OntrackDSLFacade ontrackFacade = mock(OntrackDSLFacade.class);
        MockOntrack ontrack = mock(MockOntrack.class);
        when(ontrackFacade.getDSLRoot()).thenReturn(ontrack);
        MockBuild mockBuild = mock(MockBuild.class);
        MockValidationRun mockRun = mock(MockValidationRun.class);

        when(ontrack.build("prj", "master", "1")).thenReturn(mockBuild);
        when(mockBuild.validate("VS", "FAILED")).thenReturn(mockRun);

        OntrackDSLConnector.setOntrack(ontrackFacade);

        jenkinsRule.assertBuildStatusSuccess(job.scheduleBuild2(0));

        verify(mockBuild, times(1)).validate("VS", "FAILED");
        verify(mockRun, times(1)).setRunInfo(anyMapOf(String.class, Object.class));
    }

    @Test
    public void test_validate_with_fraction_data() throws Exception {
        WorkflowJob job = jenkinsRule.jenkins.createProject(WorkflowJob.class, "workflow");
        // leave out the subject
        job.setDefinition(new CpsFlowDefinition("ontrackValidate(project: 'prj', branch: 'master', build: '1', validationStamp: 'VS', dataType: 'fraction', data: [numerator: 99, denominator: 100])", true));

        OntrackDSLFacade ontrackFacade = mock(OntrackDSLFacade.class);
        MockOntrack ontrack = mock(MockOntrack.class);
        when(ontrackFacade.getDSLRoot()).thenReturn(ontrack);
        MockBuild mockBuild = mock(MockBuild.class);
        MockValidationRun mockRun = mock(MockValidationRun.class);

        when(ontrack.build("prj", "master", "1")).thenReturn(mockBuild);
        when(mockBuild.validateWithFraction(anyString(), anyInt(), anyInt(), anyString())).thenReturn(mockRun);

        OntrackDSLConnector.setOntrack(ontrackFacade);

        jenkinsRule.assertBuildStatusSuccess(job.scheduleBuild2(0));

        verify(mockBuild, times(1)).validateWithFraction("VS", 99, 100, null);
    }

    @Test
    public void test_validate_with_fraction_data_and_status() throws Exception {
        WorkflowJob job = jenkinsRule.jenkins.createProject(WorkflowJob.class, "workflow");
        // leave out the subject
        job.setDefinition(new CpsFlowDefinition("ontrackValidate(project: 'prj', branch: 'master', build: '1', validationStamp: 'VS', validationStatus: 'FAILED', dataType: 'fraction', data: [numerator: 99, denominator: 100])", true));

        OntrackDSLFacade ontrackFacade = mock(OntrackDSLFacade.class);
        MockOntrack ontrack = mock(MockOntrack.class);
        when(ontrackFacade.getDSLRoot()).thenReturn(ontrack);
        MockBuild mockBuild = mock(MockBuild.class);
        MockValidationRun mockRun = mock(MockValidationRun.class);

        when(ontrack.build("prj", "master", "1")).thenReturn(mockBuild);
        when(mockBuild.validateWithFraction(anyString(), anyInt(), anyInt(), anyString())).thenReturn(mockRun);

        OntrackDSLConnector.setOntrack(ontrackFacade);

        jenkinsRule.assertBuildStatusSuccess(job.scheduleBuild2(0));

        verify(mockBuild, times(1)).validateWithFraction("VS", 99, 100, "FAILED");
    }

    @Test
    public void test_validate_with_metrics_data() throws Exception {
        WorkflowJob job = jenkinsRule.jenkins.createProject(WorkflowJob.class, "workflow");
        // leave out the subject
        job.setDefinition(new CpsFlowDefinition("ontrackValidate(project: 'prj', branch: 'master', build: '1', validationStamp: 'VS', dataType: 'metrics', data: [metric1: 20.12, metric2: 50])", true));

        OntrackDSLFacade ontrackFacade = mock(OntrackDSLFacade.class);
        MockOntrack ontrack = mock(MockOntrack.class);
        when(ontrackFacade.getDSLRoot()).thenReturn(ontrack);
        MockBuild mockBuild = mock(MockBuild.class);
        MockValidationRun mockRun = mock(MockValidationRun.class);

        when(ontrack.build("prj", "master", "1")).thenReturn(mockBuild);
        //noinspection unchecked
        when(mockBuild.validateWithMetrics(anyString(), anyMap(), anyString())).thenReturn(mockRun);

        OntrackDSLConnector.setOntrack(ontrackFacade);

        jenkinsRule.assertBuildStatusSuccess(job.scheduleBuild2(0));

        Map<String, Double> map = new HashMap<>();
        map.put("metric1", 20.12d);
        map.put("metric2", 50d);
        verify(mockBuild, times(1)).validateWithMetrics("VS", map, null);
    }

    @Test
    public void test_validate_with_metrics_data_and_status() throws Exception {
        WorkflowJob job = jenkinsRule.jenkins.createProject(WorkflowJob.class, "workflow");
        // leave out the subject
        job.setDefinition(new CpsFlowDefinition("ontrackValidate(project: 'prj', branch: 'master', build: '1', validationStamp: 'VS', validationStatus: 'FAILED', dataType: 'metrics', data: [metric1: 20.12, metric2: 50])", true));

        OntrackDSLFacade ontrackFacade = mock(OntrackDSLFacade.class);
        MockOntrack ontrack = mock(MockOntrack.class);
        when(ontrackFacade.getDSLRoot()).thenReturn(ontrack);
        MockBuild mockBuild = mock(MockBuild.class);
        MockValidationRun mockRun = mock(MockValidationRun.class);

        when(ontrack.build("prj", "master", "1")).thenReturn(mockBuild);
        //noinspection unchecked
        when(mockBuild.validateWithMetrics(anyString(), anyMap(), anyString())).thenReturn(mockRun);

        OntrackDSLConnector.setOntrack(ontrackFacade);

        jenkinsRule.assertBuildStatusSuccess(job.scheduleBuild2(0));

        Map<String, Double> map = new HashMap<>();
        map.put("metric1", 20.12d);
        map.put("metric2", 50d);
        verify(mockBuild, times(1)).validateWithMetrics("VS", map, "FAILED");
    }

    @Test
    public void test_validate_with_chml_data() throws Exception {
        WorkflowJob job = jenkinsRule.jenkins.createProject(WorkflowJob.class, "workflow");
        // leave out the subject
        job.setDefinition(new CpsFlowDefinition("ontrackValidate(project: 'prj', branch: 'master', build: '1', validationStamp: 'VS', dataType: 'chml', data: [critical: 12, high: 100])", true));

        OntrackDSLFacade ontrackFacade = mock(OntrackDSLFacade.class);
        MockOntrack ontrack = mock(MockOntrack.class);
        when(ontrackFacade.getDSLRoot()).thenReturn(ontrack);
        MockBuild mockBuild = mock(MockBuild.class);
        MockValidationRun mockRun = mock(MockValidationRun.class);

        when(ontrack.build("prj", "master", "1")).thenReturn(mockBuild);
        when(mockBuild.validateWithCHML(anyString(), anyInt(), anyInt(), anyInt(), anyInt(), anyString())).thenReturn(mockRun);

        OntrackDSLConnector.setOntrack(ontrackFacade);

        jenkinsRule.assertBuildStatusSuccess(job.scheduleBuild2(0));

        verify(mockBuild, times(1)).validateWithCHML("VS", 12, 100, 0, 0, null);
    }

    @Test
    public void test_validate_with_chml_data_and_status() throws Exception {
        WorkflowJob job = jenkinsRule.jenkins.createProject(WorkflowJob.class, "workflow");
        // leave out the subject
        job.setDefinition(new CpsFlowDefinition("ontrackValidate(project: 'prj', branch: 'master', build: '1', validationStamp: 'VS', validationStatus: 'FAILED', dataType: 'chml', data: [critical: 12, high: 100])", true));

        OntrackDSLFacade ontrackFacade = mock(OntrackDSLFacade.class);
        MockOntrack ontrack = mock(MockOntrack.class);
        when(ontrackFacade.getDSLRoot()).thenReturn(ontrack);
        MockBuild mockBuild = mock(MockBuild.class);
        MockValidationRun mockRun = mock(MockValidationRun.class);

        when(ontrack.build("prj", "master", "1")).thenReturn(mockBuild);
        when(mockBuild.validateWithCHML(anyString(), anyInt(), anyInt(), anyInt(), anyInt(), anyString())).thenReturn(mockRun);

        OntrackDSLConnector.setOntrack(ontrackFacade);

        jenkinsRule.assertBuildStatusSuccess(job.scheduleBuild2(0));

        verify(mockBuild, times(1)).validateWithCHML("VS", 12, 100, 0, 0, "FAILED");
    }

    @Test
    public void test_validate_with_text_data_and_status() throws Exception {
        WorkflowJob job = jenkinsRule.jenkins.createProject(WorkflowJob.class, "workflow");
        // leave out the subject
        job.setDefinition(new CpsFlowDefinition("ontrackValidate(project: 'prj', branch: 'master', build: '1', validationStamp: 'VS', validationStatus: 'FAILED', dataType: 'text', data: [value: 'My text'])", true));

        OntrackDSLFacade ontrackFacade = mock(OntrackDSLFacade.class);
        MockOntrack ontrack = mock(MockOntrack.class);
        when(ontrackFacade.getDSLRoot()).thenReturn(ontrack);
        MockBuild mockBuild = mock(MockBuild.class);
        MockValidationRun mockRun = mock(MockValidationRun.class);

        when(ontrack.build("prj", "master", "1")).thenReturn(mockBuild);
        when(mockBuild.validateWithText(anyString(), anyString(), anyString())).thenReturn(mockRun);

        OntrackDSLConnector.setOntrack(ontrackFacade);

        jenkinsRule.assertBuildStatusSuccess(job.scheduleBuild2(0));

        verify(mockBuild, times(1)).validateWithText("VS", "FAILED", "My text");
    }

    @Test
    public void test_validate_with_number_data() throws Exception {
        WorkflowJob job = jenkinsRule.jenkins.createProject(WorkflowJob.class, "workflow");
        // leave out the subject
        job.setDefinition(new CpsFlowDefinition("ontrackValidate(project: 'prj', branch: 'master', build: '1', validationStamp: 'VS', dataType: 'number', data: [value: 12])", true));

        OntrackDSLFacade ontrackFacade = mock(OntrackDSLFacade.class);
        MockOntrack ontrack = mock(MockOntrack.class);
        when(ontrackFacade.getDSLRoot()).thenReturn(ontrack);
        MockBuild mockBuild = mock(MockBuild.class);
        MockValidationRun mockRun = mock(MockValidationRun.class);

        when(ontrack.build("prj", "master", "1")).thenReturn(mockBuild);
        when(mockBuild.validateWithNumber(anyString(), anyInt(), anyString())).thenReturn(mockRun);

        OntrackDSLConnector.setOntrack(ontrackFacade);

        jenkinsRule.assertBuildStatusSuccess(job.scheduleBuild2(0));

        verify(mockBuild, times(1)).validateWithNumber("VS", 12, null);
    }

    @Test
    public void test_validate_with_number_data_and_status() throws Exception {
        WorkflowJob job = jenkinsRule.jenkins.createProject(WorkflowJob.class, "workflow");
        // leave out the subject
        job.setDefinition(new CpsFlowDefinition("ontrackValidate(project: 'prj', branch: 'master', build: '1', validationStamp: 'VS', validationStatus: 'FAILED', dataType: 'number', data: [value: 12])", true));

        OntrackDSLFacade ontrackFacade = mock(OntrackDSLFacade.class);
        MockOntrack ontrack = mock(MockOntrack.class);
        when(ontrackFacade.getDSLRoot()).thenReturn(ontrack);
        MockBuild mockBuild = mock(MockBuild.class);
        MockValidationRun mockRun = mock(MockValidationRun.class);

        when(ontrack.build("prj", "master", "1")).thenReturn(mockBuild);
        when(mockBuild.validateWithNumber(anyString(), anyInt(), anyString())).thenReturn(mockRun);

        OntrackDSLConnector.setOntrack(ontrackFacade);

        jenkinsRule.assertBuildStatusSuccess(job.scheduleBuild2(0));

        verify(mockBuild, times(1)).validateWithNumber("VS", 12, "FAILED");
    }

    @Test
    public void test_validate_with_percentage_data() throws Exception {
        WorkflowJob job = jenkinsRule.jenkins.createProject(WorkflowJob.class, "workflow");
        // leave out the subject
        job.setDefinition(new CpsFlowDefinition("ontrackValidate(project: 'prj', branch: 'master', build: '1', validationStamp: 'VS', dataType: 'percentage', data: [value: 33])", true));

        OntrackDSLFacade ontrackFacade = mock(OntrackDSLFacade.class);
        MockOntrack ontrack = mock(MockOntrack.class);
        when(ontrackFacade.getDSLRoot()).thenReturn(ontrack);
        MockBuild mockBuild = mock(MockBuild.class);
        MockValidationRun mockRun = mock(MockValidationRun.class);

        when(ontrack.build("prj", "master", "1")).thenReturn(mockBuild);
        when(mockBuild.validateWithPercentage(anyString(), anyInt(), anyString())).thenReturn(mockRun);

        OntrackDSLConnector.setOntrack(ontrackFacade);

        jenkinsRule.assertBuildStatusSuccess(job.scheduleBuild2(0));

        verify(mockBuild, times(1)).validateWithPercentage("VS", 33, null);
    }

    @Test
    public void test_validate_with_percentage_data_and_status() throws Exception {
        WorkflowJob job = jenkinsRule.jenkins.createProject(WorkflowJob.class, "workflow");
        // leave out the subject
        job.setDefinition(new CpsFlowDefinition("ontrackValidate(project: 'prj', branch: 'master', build: '1', validationStamp: 'VS', validationStatus: 'FAILED', dataType: 'percentage', data: [value: 33])", true));

        OntrackDSLFacade ontrackFacade = mock(OntrackDSLFacade.class);
        MockOntrack ontrack = mock(MockOntrack.class);
        when(ontrackFacade.getDSLRoot()).thenReturn(ontrack);
        MockBuild mockBuild = mock(MockBuild.class);
        MockValidationRun mockRun = mock(MockValidationRun.class);

        when(ontrack.build("prj", "master", "1")).thenReturn(mockBuild);
        when(mockBuild.validateWithPercentage(anyString(), anyInt(), anyString())).thenReturn(mockRun);

        OntrackDSLConnector.setOntrack(ontrackFacade);

        jenkinsRule.assertBuildStatusSuccess(job.scheduleBuild2(0));

        verify(mockBuild, times(1)).validateWithPercentage("VS", 33, "FAILED");
    }

    @Test
    public void test_validate_with_generic_data() throws Exception {
        WorkflowJob job = jenkinsRule.jenkins.createProject(WorkflowJob.class, "workflow");
        // leave out the subject
        job.setDefinition(new CpsFlowDefinition("ontrackValidate(project: 'prj', branch: 'master', build: '1', validationStamp: 'VS', dataType: 'net.nemerosa.ontrack.extension.general.validation.CHMLValidationDataType', data: [value: 33])", true));

        OntrackDSLFacade ontrackFacade = mock(OntrackDSLFacade.class);
        MockOntrack ontrack = mock(MockOntrack.class);
        when(ontrackFacade.getDSLRoot()).thenReturn(ontrack);
        MockBuild mockBuild = mock(MockBuild.class);
        MockValidationRun mockRun = mock(MockValidationRun.class);

        when(ontrack.build("prj", "master", "1")).thenReturn(mockBuild);
        when(mockBuild.validateWithData(anyString(), any(), anyString(), anyString())).thenReturn(mockRun);

        OntrackDSLConnector.setOntrack(ontrackFacade);

        jenkinsRule.assertBuildStatusSuccess(job.scheduleBuild2(0));

        verify(mockBuild, times(1)).validateWithData("VS", ImmutableMap.of("value", 33), "net.nemerosa.ontrack.extension.general.validation.CHMLValidationDataType", null);
    }

    @Test
    public void test_validate_with_generic_data_and_status() throws Exception {
        WorkflowJob job = jenkinsRule.jenkins.createProject(WorkflowJob.class, "workflow");
        // leave out the subject
        job.setDefinition(new CpsFlowDefinition("ontrackValidate(project: 'prj', branch: 'master', build: '1', validationStamp: 'VS', validationStatus: 'FAILED', dataType: 'net.nemerosa.ontrack.extension.general.validation.CHMLValidationDataType', data: [value: 33])", true));

        OntrackDSLFacade ontrackFacade = mock(OntrackDSLFacade.class);
        MockOntrack ontrack = mock(MockOntrack.class);
        when(ontrackFacade.getDSLRoot()).thenReturn(ontrack);
        MockBuild mockBuild = mock(MockBuild.class);
        MockValidationRun mockRun = mock(MockValidationRun.class);

        when(ontrack.build("prj", "master", "1")).thenReturn(mockBuild);
        when(mockBuild.validateWithData(anyString(), any(), anyString(), anyString())).thenReturn(mockRun);

        OntrackDSLConnector.setOntrack(ontrackFacade);

        jenkinsRule.assertBuildStatusSuccess(job.scheduleBuild2(0));

        verify(mockBuild, times(1)).validateWithData("VS", ImmutableMap.of("value", 33), "net.nemerosa.ontrack.extension.general.validation.CHMLValidationDataType", "FAILED");
    }

}
