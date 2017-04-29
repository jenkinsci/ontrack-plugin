package net.nemerosa.ontrack.jenkins.steps;

import hudson.model.Result;
import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.jenkinsci.plugins.workflow.job.WorkflowRun;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

public class OntrackBuildStepRunTest {

    @Rule
    public JenkinsRule jenkinsRule = new JenkinsRule();

    @Test
    public void test_missing_project() throws Exception {
        WorkflowJob job = jenkinsRule.jenkins.createProject(WorkflowJob.class, "workflow");
        // leave out the subject
        job.setDefinition(new CpsFlowDefinition("ontrackBuild(branch: 'master', build: '1')", true));

        WorkflowRun run = jenkinsRule.assertBuildStatus(Result.FAILURE, job.scheduleBuild2(0).get());
        jenkinsRule.assertLogContains("Argument for @Nonnull parameter 'project' of net/nemerosa/ontrack/jenkins/steps/OntrackBuildStep.<init> must not be null", run);
    }

    @Test
    public void test_missing_branch() throws Exception {
        WorkflowJob job = jenkinsRule.jenkins.createProject(WorkflowJob.class, "workflow");
        // leave out the subject
        job.setDefinition(new CpsFlowDefinition("ontrackBuild(project: 'proj', build: '1')", true));

        WorkflowRun run = jenkinsRule.assertBuildStatus(Result.FAILURE, job.scheduleBuild2(0).get());
        jenkinsRule.assertLogContains("Argument for @Nonnull parameter 'branch' of net/nemerosa/ontrack/jenkins/steps/OntrackBuildStep.<init> must not be null", run);
    }

    @Test
    public void test_missing_build() throws Exception {
        WorkflowJob job = jenkinsRule.jenkins.createProject(WorkflowJob.class, "workflow");
        // leave out the subject
        job.setDefinition(new CpsFlowDefinition("ontrackBuild(project: 'proj', branch: 'master')", true));

        WorkflowRun run = jenkinsRule.assertBuildStatus(Result.FAILURE, job.scheduleBuild2(0).get());
        jenkinsRule.assertLogContains("Argument for @Nonnull parameter 'build' of net/nemerosa/ontrack/jenkins/steps/OntrackBuildStep.<init> must not be null", run);
    }

    @Test
    public void test_blank_project() throws Exception {
        WorkflowJob job = jenkinsRule.jenkins.createProject(WorkflowJob.class, "workflow");
        // leave out the subject
        job.setDefinition(new CpsFlowDefinition("ontrackBuild(project: '', branch: 'master', build: '1')", true));

        WorkflowRun run = jenkinsRule.assertBuildStatus(Result.FAILURE, job.scheduleBuild2(0).get());
        jenkinsRule.assertLogContains("Ontrack build not created. All mandatory properties must be supplied ('project', 'branch', 'build').", run);
    }

}
