package net.nemerosa.ontrack.jenkins.steps;

import com.google.common.collect.ImmutableSet;
import hudson.AbortException;
import hudson.Extension;
import hudson.model.TaskListener;
import net.nemerosa.ontrack.dsl.Ontrack;
import net.nemerosa.ontrack.jenkins.dsl.OntrackDSLConnector;
import net.sf.json.JSON;
import net.sf.json.JSONSerializer;
import org.jenkinsci.plugins.workflow.steps.*;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import static org.apache.commons.lang.StringUtils.isBlank;

/**
 * Step run a GraphQL script
 */
@SuppressWarnings("unused")
public class OntrackGraphQLStep extends Step {

    /**
     * Script to run
     */
    private final String script;

    /**
     * Bindings
     */
    private Map<String, Object> bindings = Collections.emptyMap();

    @DataBoundConstructor
    public OntrackGraphQLStep(String script) {
        this.script = script;
    }

    public String getScript() {
        return script;
    }

    public Map<String, Object> getBindings() {
        return bindings;
    }

    @DataBoundSetter
    public void setBindings(Map<String, Object> bindings) {
        this.bindings = bindings;
    }

    @Override
    public StepExecution start(final StepContext context) throws Exception {
        // Checks
        if (isBlank(script)) {
            throw new AbortException("Ontrack script not run. All mandatory properties must be supplied ('script').");
        }
        // OK
        return new SynchronousNonBlockingStepExecution<JSON>(context) {
            @Override
            protected JSON run() throws Exception {
                // Context
                TaskListener listener = context.get(TaskListener.class);
                assert listener != null;
                // Gets the Ontrack connector
                Ontrack ontrack = OntrackDSLConnector.createOntrackConnector(listener);
                // Query
                Object result = ontrack.graphQLQuery(script, bindings);
                // Returns result as JSON
                return JSONSerializer.toJSON(result);
            }
        };
    }

    @Extension
    public static class DescriptorImpl extends StepDescriptor {

        @Override
        public Set<? extends Class<?>> getRequiredContext() {
            return ImmutableSet.of(TaskListener.class);
        }

        @Override
        public String getFunctionName() {
            return "ontrackGraphQL";
        }

        @Nonnull
        @Override
        public String getDisplayName() {
            return "Runs some Ontrack GraphQL script";
        }
    }

}
