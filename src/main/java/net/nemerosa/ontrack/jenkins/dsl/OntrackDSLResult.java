package net.nemerosa.ontrack.jenkins.dsl;

import hudson.model.Result;

public final class OntrackDSLResult {

    private OntrackDSLResult() {
    }

    public static Result toJenkinsResult(Object shellResult) {
        if (shellResult instanceof String && !shellResult.equals("")) {
            return Result.FAILURE;
        } else if (shellResult instanceof Boolean) {
            return (Boolean) shellResult ? Result.SUCCESS : Result.FAILURE;
        } else {
            return Result.SUCCESS;
        }
    }

}
