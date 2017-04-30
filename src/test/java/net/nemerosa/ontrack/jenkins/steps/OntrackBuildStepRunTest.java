package net.nemerosa.ontrack.jenkins.steps;

import hudson.model.Result;
import net.nemerosa.ontrack.dsl.Branch;
import net.nemerosa.ontrack.dsl.Ontrack;
import net.nemerosa.ontrack.jenkins.dsl.OntrackDSLConnector;
import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.jenkinsci.plugins.workflow.job.WorkflowRun;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import static org.mockito.Mockito.*;

public class OntrackBuildStepRunTest {

    @Rule
    public JenkinsRule jenkinsRule = new JenkinsRule();

    @Test
    public void test_blank_project() throws Exception {
        WorkflowJob job = jenkinsRule.jenkins.createProject(WorkflowJob.class, "workflow");
        // leave out the subject
        job.setDefinition(new CpsFlowDefinition("ontrackBuild(project: '', branch: 'master', build: '1')", true));

        WorkflowRun run = jenkinsRule.assertBuildStatus(Result.FAILURE, job.scheduleBuild2(0).get());
        jenkinsRule.assertLogContains("Ontrack build not created. All mandatory properties must be supplied ('project', 'branch', 'build').", run);
    }

    @Test
    public void test_blank_branch() throws Exception {
        WorkflowJob job = jenkinsRule.jenkins.createProject(WorkflowJob.class, "workflow");
        // leave out the subject
        job.setDefinition(new CpsFlowDefinition("ontrackBuild(project: 'prj', branch: '', build: '1')", true));

        WorkflowRun run = jenkinsRule.assertBuildStatus(Result.FAILURE, job.scheduleBuild2(0).get());
        jenkinsRule.assertLogContains("Ontrack build not created. All mandatory properties must be supplied ('project', 'branch', 'build').", run);
    }

    @Test
    public void test_blank_build() throws Exception {
        WorkflowJob job = jenkinsRule.jenkins.createProject(WorkflowJob.class, "workflow");
        // leave out the subject
        job.setDefinition(new CpsFlowDefinition("ontrackBuild(project: 'prj', branch: 'master', build: '')", true));

        WorkflowRun run = jenkinsRule.assertBuildStatus(Result.FAILURE, job.scheduleBuild2(0).get());
        jenkinsRule.assertLogContains("Ontrack build not created. All mandatory properties must be supplied ('project', 'branch', 'build').", run);
    }

    @Test
    public void test_build_create() throws Exception {
        WorkflowJob job = jenkinsRule.jenkins.createProject(WorkflowJob.class, "workflow");
        // leave out the subject
        job.setDefinition(new CpsFlowDefinition("ontrackBuild(project: 'prj', branch: 'master', build: '1')", true));

        Ontrack ontrack = mock(Ontrack.class);
        Branch mockBranch = mock(Branch.class);

        when(ontrack.branch("prj", "master")).thenReturn(mockBranch);

        OntrackDSLConnector.setOntrack(ontrack);

        jenkinsRule.assertBuildStatusSuccess(job.scheduleBuild2(0));

        verify(mockBranch, times(1)).build("1", "Build 1", true);
    }

}
