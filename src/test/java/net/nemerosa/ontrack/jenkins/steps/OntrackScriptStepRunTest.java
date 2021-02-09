package net.nemerosa.ontrack.jenkins.steps;

import net.nemerosa.ontrack.jenkins.dsl.OntrackDSLConnector;
import net.nemerosa.ontrack.jenkins.dsl.OntrackDSLFacade;
import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import java.util.Collections;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OntrackScriptStepRunTest {

    @Rule
    public JenkinsRule jenkinsRule = new JenkinsRule();

    @Test
    public void test_script() throws Exception {
        WorkflowJob job = jenkinsRule.jenkins.createProject(WorkflowJob.class, "workflow");
        // leave out the subject
        job.setDefinition(new CpsFlowDefinition("ontrackScript(script: 'ontrack.projects*.name')", true));

        OntrackDSLFacade ontrack = mock(OntrackDSLFacade.class);
        when(ontrack.getDSLRoot()).thenReturn(Collections.singletonMap(
                "projects", Collections.singletonList(
                        Collections.singletonMap(
                                "name", "MYPRJ"
                        )
                )
        ));

        OntrackDSLConnector.setOntrack(ontrack);

        jenkinsRule.assertBuildStatusSuccess(job.scheduleBuild2(0));
    }

}
