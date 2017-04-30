package net.nemerosa.ontrack.jenkins.steps;

import hudson.model.Result;
import net.nemerosa.ontrack.dsl.Branch;
import net.nemerosa.ontrack.dsl.Build;
import net.nemerosa.ontrack.dsl.Ontrack;
import net.nemerosa.ontrack.jenkins.dsl.OntrackDSLConnector;
import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.jenkinsci.plugins.workflow.job.WorkflowRun;
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

        when(ontrack.build("prj", "master", "1")).thenReturn(mockBuild);

        OntrackDSLConnector.setOntrack(ontrack);

        jenkinsRule.assertBuildStatusSuccess(job.scheduleBuild2(0));

        verify(mockBuild, times(1)).validate("VS", "PASSED");
    }

    @Test
    public void test_validate_custom() throws Exception {
        WorkflowJob job = jenkinsRule.jenkins.createProject(WorkflowJob.class, "workflow");
        // leave out the subject
        job.setDefinition(new CpsFlowDefinition("ontrackValidate(project: 'prj', branch: 'master', build: '1', validationStamp: 'VS', validationStatus: 'FAILED')", true));

        Ontrack ontrack = mock(Ontrack.class);
        Build mockBuild = mock(Build.class);

        when(ontrack.build("prj", "master", "1")).thenReturn(mockBuild);

        OntrackDSLConnector.setOntrack(ontrack);

        jenkinsRule.assertBuildStatusSuccess(job.scheduleBuild2(0));

        verify(mockBuild, times(1)).validate("VS", "FAILED");
    }

}
