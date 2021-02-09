package net.nemerosa.ontrack.jenkins.steps;

import net.nemerosa.ontrack.jenkins.MockOntrack;
import net.nemerosa.ontrack.jenkins.dsl.OntrackDSLConnector;
import net.nemerosa.ontrack.jenkins.dsl.OntrackDSLFacade;
import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import java.util.Collections;

import static org.mockito.Mockito.*;

public class OntrackGraphQLStepRunTest {

    @Rule
    public JenkinsRule jenkinsRule = new JenkinsRule();

    @Test
    public void test_script() throws Exception {
        WorkflowJob job = jenkinsRule.jenkins.createProject(WorkflowJob.class, "workflow");
        // leave out the subject
        job.setDefinition(new CpsFlowDefinition("ontrackGraphQL(script: '{projects{name}}')", true));

        OntrackDSLFacade ontrackFacade = mock(OntrackDSLFacade.class);
        MockOntrack ontrack = mock(MockOntrack.class);
        when(ontrackFacade.getDSLRoot()).thenReturn(ontrack);

        OntrackDSLConnector.setOntrack(ontrackFacade);

        jenkinsRule.assertBuildStatusSuccess(job.scheduleBuild2(0));

        verify(ontrack, times(1)).graphQLQuery("{projects{name}}", Collections.<String, Object>emptyMap());
    }

}
