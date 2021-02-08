package net.nemerosa.ontrack.jenkins.steps;

import com.google.common.collect.ImmutableSet;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import hudson.AbortException;
import hudson.Extension;
import hudson.model.Run;
import hudson.model.TaskListener;
import net.nemerosa.ontrack.jenkins.dsl.JenkinsConnector;
import net.nemerosa.ontrack.jenkins.dsl.OntrackDSLConnector;
import net.nemerosa.ontrack.jenkins.dsl.OntrackDSLFacade;
import net.sf.json.JSON;
import net.sf.json.JSONSerializer;
import org.jenkinsci.plugins.workflow.steps.*;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.apache.commons.lang.StringUtils.isBlank;

/**
 * Step to run a DSL script
 */
@SuppressWarnings("unused")
public class OntrackScriptStep extends Step implements Serializable {

    private static final long serialVersionUID = 42L;

    /**
     * Script to run
     */
    private final String script;

    /**
     * Bindings
     */
    private Map<String, Object> bindings = Collections.emptyMap();

    /**
     * Logging
     */
    private boolean logging = false;

    @DataBoundConstructor
    public OntrackScriptStep(String script) {
        this.script = script;
    }

    public String getScript() {
        return script;
    }

    public Map<String, Object> getBindings() {
        return bindings;
    }

    public boolean isLogging() {
        return logging;
    }

    @DataBoundSetter
    public void setBindings(Map<String, Object> bindings) {
        this.bindings = bindings;
    }

    @DataBoundSetter
    public void setLogging(boolean logging) {
        this.logging = logging;
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
                OntrackDSLFacade ontrack = OntrackDSLConnector.createOntrackConnector(listener);
                // Values to bind
                Map<String, Object> values = new HashMap<>(bindings);
                // Jenkins connector
                Run run = context.get(Run.class);
                if (run != null) {
                    values.put("jenkins", new JenkinsConnector(
                            run,
                            listener,
                            runInfo -> OntrackStepHelper.adaptRunInfo(context, runInfo)
                    ));
                }
                // Binding
                values.put("ontrack", ontrack.getDSLRoot());
                values.put("out", listener.getLogger());
                Binding binding = new Binding(values);
                // Groovy shell
                GroovyShell shell = new GroovyShell(binding);
                // Runs the script
                listener.getLogger().format("[ontrack] DSL script running...%n");
                Object shellResult = shell.evaluate(script);
                if (logging) {
                    listener.getLogger().format("[ontrack] DSL script returned result: %s%n", shellResult);
                } else {
                    listener.getLogger().format("[ontrack] DSL script returned result.%n");
                }
                // Returns result as JSON
                return JSONSerializer.toJSON(shellResult);
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
            return "ontrackScript";
        }

        @Nonnull
        @Override
        public String getDisplayName() {
            return "Runs some Ontrack DSL script";
        }
    }

}
