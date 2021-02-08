package net.nemerosa.ontrack.jenkins.dsl.facade

/**
 * DSL facade for a validation run
 */
interface ValidationRunFacade {

    /**
     * Sets some run info on this run.
     *
     * @param runInfo Run info to set
     */
    void setRunInfo(Map<String, ?> runInfo)

    /**
     * Gets the last validation run status
     * @return A validation run status
     */
    ValidationRunStatusFacade getLastValidationRunStatus()
}