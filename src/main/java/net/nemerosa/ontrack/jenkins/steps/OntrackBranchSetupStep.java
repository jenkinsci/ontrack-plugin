package net.nemerosa.ontrack.jenkins.steps;

import com.google.common.collect.ImmutableSet;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import hudson.AbortException;
import hudson.Extension;
import hudson.model.TaskListener;
import net.nemerosa.ontrack.jenkins.dsl.OntrackDSLConnector;
import net.nemerosa.ontrack.jenkins.dsl.OntrackDSLFacade;
import net.nemerosa.ontrack.jenkins.dsl.facade.BranchFacade;
import net.nemerosa.ontrack.jenkins.dsl.facade.ProjectFacade;
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
 * Step to setup an Ontrack branch, and create it if it does not exist.
 */
@SuppressWarnings("unused")
public class OntrackBranchSetupStep extends Step implements Serializable {

    private static final long serialVersionUID = 42L;

    /**
     * Project name
     */
    private final String project;

    /**
     * Branch name
     */
    private final String branch;

    /**
     * Script to run to configure the branch, with both `ontrack` and `branch` being injected.
     */
    private final String script;

    /**
     * Logging
     */
    private boolean logging = false;

    /**
     * Additional bindings
     */
    private Map<String, Object> bindings = Collections.emptyMap();

    @DataBoundConstructor
    public OntrackBranchSetupStep(String project, String branch, String script) {
        this.project = project;
        this.branch = branch;
        this.script = script;
    }

    public String getProject() {
        return project;
    }

    public String getBranch() {
        return branch;
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

    public boolean isLogging() {
        return logging;
    }

    @DataBoundSetter
    public void setLogging(boolean logging) {
        this.logging = logging;
    }

    @Override
    public StepExecution start(final StepContext context) throws Exception {
        // Checks
        if (isBlank(script)) {
            throw new AbortException("Ontrack branch setup not run. All mandatory properties must be supplied ('project', 'branch', 'script').");
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
                // Gets the project
                ProjectFacade ontrackProject = ontrack.project(project);
                // Gets the branch and creates it if it does not exist
                BranchFacade ontrackBranch = ontrackProject.branch(branch, "", true);
                // Values to bind
                Map<String, Object> values = new HashMap<>(bindings);
                // Binding
                values.put("ontrack", ontrack.getDSLRoot());
                values.put("branch", ontrackBranch.getDSLRoot());
                values.put("out", listener.getLogger());
                Binding binding = new Binding(values);
                // Groovy shell
                GroovyShell shell = new GroovyShell(binding);
                // Runs the script
                listener.getLogger().format("[ontrack] Branch setup DSL script running...%n");
                Object shellResult = shell.evaluate(script);
                if (logging) {
                    listener.getLogger().format("[ontrack] Branch setup DSL script returned result: %s%n", shellResult);
                } else {
                    listener.getLogger().format("[ontrack] Branch setup DSL script returned result.%n");
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
            return "ontrackBranchSetup";
        }

        @Nonnull
        @Override
        public String getDisplayName() {
            return "Setup an Ontrack branch, and creates it if it does not exist.";
        }
    }

}
