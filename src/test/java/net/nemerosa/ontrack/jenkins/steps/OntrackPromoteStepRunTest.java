package net.nemerosa.ontrack.jenkins.steps;

import net.nemerosa.ontrack.jenkins.dsl.OntrackDSLConnector;
import net.nemerosa.ontrack.jenkins.dsl.OntrackDSLFacade;
import net.nemerosa.ontrack.jenkins.dsl.facade.BuildFacade;
import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import static org.mockito.Mockito.*;

public class OntrackPromoteStepRunTest {

    @Rule
    public JenkinsRule jenkinsRule = new JenkinsRule();

    @Test
    public void test_promote() throws Exception {
        WorkflowJob job = jenkinsRule.jenkins.createProject(WorkflowJob.class, "workflow");
        // leave out the subject
        job.setDefinition(new CpsFlowDefinition("ontrackPromote(project: 'prj', branch: 'master', build: '1', promotionLevel: 'PL')", true));

        OntrackDSLFacade ontrackFacade = mock(OntrackDSLFacade.class);
        BuildFacade mockBuild = mock(BuildFacade.class);

        when(ontrackFacade.build("prj", "master", "1")).thenReturn(mockBuild);

        OntrackDSLConnector.setOntrack(ontrackFacade);

        jenkinsRule.assertBuildStatusSuccess(job.scheduleBuild2(0));

        verify(mockBuild, times(1)).promote("PL");
    }

}
