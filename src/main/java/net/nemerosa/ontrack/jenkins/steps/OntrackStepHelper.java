package net.nemerosa.ontrack.jenkins.steps;

import hudson.model.Run;
import net.nemerosa.ontrack.jenkins.OntrackPluginSupport;
import org.jenkinsci.plugins.workflow.actions.TimingAction;
import org.jenkinsci.plugins.workflow.cps.nodes.StepStartNode;
import org.jenkinsci.plugins.workflow.graph.FlowNode;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.StepDescriptor;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class OntrackStepHelper {

    public static @Nullable
    Map<String, ?> getRunInfo(StepContext context) throws IOException, InterruptedException {
        // Gets the associated run
        Run run = context.get(Run.class);
        if (run == null) {
            // No run? Getting away from there...
            return null;
        }
        // Base run info
        Map<String, Object> runInfo = OntrackPluginSupport.getRunInfo(run);
        // Gets the (current) duration of the stage
        FlowNode flowNode = context.get(FlowNode.class);
        if (flowNode != null) {
            Long durationSeconds = getTiming(flowNode);
            if (durationSeconds != null) {
                runInfo.put("runTime", durationSeconds);
            }
        }
        // Run info if not empty
        if (runInfo.isEmpty()) {
            return null;
        } else {
            return runInfo;
        }
    }

    private static Long getTiming(FlowNode node) {
        if (node instanceof StepStartNode) {
            StepStartNode stepNode = (StepStartNode) node;
            StepDescriptor stepDescriptor = stepNode.getDescriptor();
            if (stepDescriptor != null) {
                String stepDescriptorId = stepDescriptor.getId();
                if ("org.jenkinsci.plugins.workflow.support.steps.StageStep".equals(stepDescriptorId)) {
                    TimingAction timingAction = node.getAction(TimingAction.class);
                    if (timingAction != null) {
                        long startTime = timingAction.getStartTime();
                        return (System.currentTimeMillis() - startTime) / 1000;
                    }
                }
            }
        }
        return getTiming(node.getParents());
    }

    private static Long getTiming(List<FlowNode> nodes) {
        for (FlowNode node : nodes) {
            Long durationSeconds = getTiming(node);
            if (durationSeconds != null) {
                return durationSeconds;
            }
        }
        return null;
    }

}
