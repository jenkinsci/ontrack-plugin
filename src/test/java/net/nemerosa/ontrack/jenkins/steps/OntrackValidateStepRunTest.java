package net.nemerosa.ontrack.jenkins.steps;

import net.nemerosa.ontrack.dsl.Build;
import net.nemerosa.ontrack.dsl.Ontrack;
import net.nemerosa.ontrack.dsl.ValidationRun;
import net.nemerosa.ontrack.jenkins.dsl.OntrackDSLConnector;
import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import static org.mockito.Mockito.*;

public class OntrackValidateStepRunTest {

    @Rule
    public JenkinsRule jenkinsRule = new JenkinsRule();

    @Test
    public void test_validate_default() throws Exception {
        WorkflowJob job = jenkinsRule.jenkins.createProject(WorkflowJob.class, "workflow");
        // leave out the subject
        job.setDefinition(new CpsFlowDefinition("ontrackValidate(project: 'prj', branch: 'master', build: '1', validationStamp: 'VS')", true));

        Ontrack ontrack = mock(Ontrack.class);
        Build mockBuild = mock(Build.class);
        ValidationRun mockRun = mock(ValidationRun.class);

        when(ontrack.build("prj", "master", "1")).thenReturn(mockBuild);
        when(mockBuild.validate("VS", "PASSED")).thenReturn(mockRun);

        OntrackDSLConnector.setOntrack(ontrack);

        jenkinsRule.assertBuildStatusSuccess(job.scheduleBuild2(0));

        verify(mockBuild, times(1)).validate("VS", "PASSED");
        verify(mockRun, times(1)).setRunInfo(anyMapOf(String.class, Object.class));
    }

    @Test
    public void test_validate_custom() throws Exception {
        WorkflowJob job = jenkinsRule.jenkins.createProject(WorkflowJob.class, "workflow");
        // leave out the subject
        job.setDefinition(new CpsFlowDefinition("ontrackValidate(project: 'prj', branch: 'master', build: '1', validationStamp: 'VS', validationStatus: 'FAILED')", true));

        Ontrack ontrack = mock(Ontrack.class);
        Build mockBuild = mock(Build.class);
        ValidationRun mockRun = mock(ValidationRun.class);

        when(ontrack.build("prj", "master", "1")).thenReturn(mockBuild);
        when(mockBuild.validate("VS", "FAILED")).thenReturn(mockRun);

        OntrackDSLConnector.setOntrack(ontrack);

        jenkinsRule.assertBuildStatusSuccess(job.scheduleBuild2(0));

        verify(mockBuild, times(1)).validate("VS", "FAILED");
        verify(mockRun, times(1)).setRunInfo(anyMapOf(String.class, Object.class));
    }

    @Test
    public void test_validate_with_fraction_data() throws Exception {
        WorkflowJob job = jenkinsRule.jenkins.createProject(WorkflowJob.class, "workflow");
        // leave out the subject
        job.setDefinition(new CpsFlowDefinition("ontrackValidate(project: 'prj', branch: 'master', build: '1', validationStamp: 'VS', dataType: 'fraction', data: [numerator: 99, denominator: 100])", true));

        Ontrack ontrack = mock(Ontrack.class);
        Build mockBuild = mock(Build.class);
        ValidationRun mockRun = mock(ValidationRun.class);

        when(ontrack.build("prj", "master", "1")).thenReturn(mockBuild);
        when(mockBuild.validateWithFraction(anyString(), anyInt(), anyInt(), anyString())).thenReturn(mockRun);

        OntrackDSLConnector.setOntrack(ontrack);

        jenkinsRule.assertBuildStatusSuccess(job.scheduleBuild2(0));

        verify(mockBuild, times(1)).validateWithFraction("VS", 99, 100, null);
    }

    @Test
    public void test_validate_with_fraction_data_and_status() throws Exception {
        WorkflowJob job = jenkinsRule.jenkins.createProject(WorkflowJob.class, "workflow");
        // leave out the subject
        job.setDefinition(new CpsFlowDefinition("ontrackValidate(project: 'prj', branch: 'master', build: '1', validationStamp: 'VS', validationStatus: 'FAILED', dataType: 'fraction', data: [numerator: 99, denominator: 100])", true));

        Ontrack ontrack = mock(Ontrack.class);
        Build mockBuild = mock(Build.class);
        ValidationRun mockRun = mock(ValidationRun.class);

        when(ontrack.build("prj", "master", "1")).thenReturn(mockBuild);
        when(mockBuild.validateWithFraction(anyString(), anyInt(), anyInt(), anyString())).thenReturn(mockRun);

        OntrackDSLConnector.setOntrack(ontrack);

        jenkinsRule.assertBuildStatusSuccess(job.scheduleBuild2(0));

        verify(mockBuild, times(1)).validateWithFraction("VS", 99, 100, "FAILED");
    }

    @Test
    public void test_validate_with_chml_data() throws Exception {
        WorkflowJob job = jenkinsRule.jenkins.createProject(WorkflowJob.class, "workflow");
        // leave out the subject
        job.setDefinition(new CpsFlowDefinition("ontrackValidate(project: 'prj', branch: 'master', build: '1', validationStamp: 'VS', dataType: 'chml', data: [critical: 12, high: 100])", true));

        Ontrack ontrack = mock(Ontrack.class);
        Build mockBuild = mock(Build.class);
        ValidationRun mockRun = mock(ValidationRun.class);

        when(ontrack.build("prj", "master", "1")).thenReturn(mockBuild);
        when(mockBuild.validateWithCHML(anyString(), anyInt(), anyInt(), anyInt(), anyInt(), anyString())).thenReturn(mockRun);

        OntrackDSLConnector.setOntrack(ontrack);

        jenkinsRule.assertBuildStatusSuccess(job.scheduleBuild2(0));

        verify(mockBuild, times(1)).validateWithCHML("VS", 12, 100, 0, 0, null);
    }

    @Test
    public void test_validate_with_chml_data_and_status() throws Exception {
        WorkflowJob job = jenkinsRule.jenkins.createProject(WorkflowJob.class, "workflow");
        // leave out the subject
        job.setDefinition(new CpsFlowDefinition("ontrackValidate(project: 'prj', branch: 'master', build: '1', validationStamp: 'VS', validationStatus: 'FAILED', dataType: 'chml', data: [critical: 12, high: 100])", true));

        Ontrack ontrack = mock(Ontrack.class);
        Build mockBuild = mock(Build.class);
        ValidationRun mockRun = mock(ValidationRun.class);

        when(ontrack.build("prj", "master", "1")).thenReturn(mockBuild);
        when(mockBuild.validateWithCHML(anyString(), anyInt(), anyInt(), anyInt(), anyInt(), anyString())).thenReturn(mockRun);

        OntrackDSLConnector.setOntrack(ontrack);

        jenkinsRule.assertBuildStatusSuccess(job.scheduleBuild2(0));

        verify(mockBuild, times(1)).validateWithCHML("VS", 12, 100, 0, 0, "FAILED");
    }

    @Test
    public void test_validate_with_text_data_and_status() throws Exception {
        WorkflowJob job = jenkinsRule.jenkins.createProject(WorkflowJob.class, "workflow");
        // leave out the subject
        job.setDefinition(new CpsFlowDefinition("ontrackValidate(project: 'prj', branch: 'master', build: '1', validationStamp: 'VS', validationStatus: 'FAILED', dataType: 'text', data: [value: 'My text'])", true));

        Ontrack ontrack = mock(Ontrack.class);
        Build mockBuild = mock(Build.class);
        ValidationRun mockRun = mock(ValidationRun.class);

        when(ontrack.build("prj", "master", "1")).thenReturn(mockBuild);
        when(mockBuild.validateWithText(anyString(), anyString(), anyString())).thenReturn(mockRun);

        OntrackDSLConnector.setOntrack(ontrack);

        jenkinsRule.assertBuildStatusSuccess(job.scheduleBuild2(0));

        verify(mockBuild, times(1)).validateWithText("VS", "FAILED", "My text");
    }

}
