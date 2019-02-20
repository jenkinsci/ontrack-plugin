package net.nemerosa.ontrack.jenkins;

import antlr.ANTLRException;
import hudson.Extension;
import hudson.model.Item;
import hudson.model.Job;
import hudson.triggers.Trigger;
import hudson.triggers.TriggerDescriptor;
import net.nemerosa.ontrack.jenkins.trigger.TriggerDefinition;
import net.nemerosa.ontrack.jenkins.trigger.TriggerHelper;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Trigger based on builds and promotions, for several projects and branches.
 */
public class OntrackMultiTrigger extends Trigger<Job> {

    private static final Logger LOGGER = Logger.getLogger(OntrackMultiTrigger.class.getName());

    private static final Level LOG_LEVEL = Level.FINE;
    public static final String SUCCESS = "SUCCESS";
    public static final String FAILURE = "FAILURE";
    public static final String UNSTABLE = "UNSTABLE";

    /**
     * Trigger definitions
     */
    private final List<TriggerDefinition> triggers;

    /**
     * Constructor.
     *
     * @param spec     CRON specification
     * @param triggers Trigger definitions
     * @throws ANTLRException If CRON expression is not correct
     */
    @DataBoundConstructor
    public OntrackMultiTrigger(String spec, List<TriggerDefinition> triggers) throws ANTLRException {
        super(spec);
        this.triggers = triggers;
    }

    public List<TriggerDefinition> getTriggers() {
        return triggers;
    }

    @Override
    public void run() {
        // Checks for null
        if (job == null) {
            LOGGER.log(Level.WARNING, "[ontrack][trigger] Cannot run because of job being null.");
            return;
        }
        // Logging
        LOGGER.log(LOG_LEVEL, String.format("[ontrack][trigger][%s] Check triggers", job.getFullName()));

        // Evaluates the trigger
        TriggerHelper.evaluate(job, triggers);

    }

    @Extension
    @Symbol("ontrackMultiTrigger")
    public static class DescriptorImpl extends TriggerDescriptor {

        @Override
        public boolean isApplicable(Item item) {
            return item instanceof Job;
        }

        @Override
        public String getDisplayName() {
            return "Ontrack: multi trigger";
        }
    }

}
