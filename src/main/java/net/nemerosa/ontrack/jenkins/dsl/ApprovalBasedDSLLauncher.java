package net.nemerosa.ontrack.jenkins.dsl;

import groovy.lang.GroovyCodeSource;
import hudson.model.Item;
import org.jenkinsci.plugins.scriptsecurity.sandbox.RejectedAccessException;
import org.jenkinsci.plugins.scriptsecurity.scripts.ApprovalContext;
import org.jenkinsci.plugins.scriptsecurity.scripts.ScriptApproval;
import org.jenkinsci.plugins.scriptsecurity.scripts.languages.GroovyLanguage;

public class ApprovalBasedDSLLauncher extends AbstractDSLLauncher {

    private final Item source;

    public ApprovalBasedDSLLauncher(Item source) {
        this.source = source;
    }

    @Override
    protected GroovyCodeSource prepareGroovyCodeSource(String dsl) {
        ScriptApproval.get()
                .configuring(
                        dsl,
                        GroovyLanguage.get(),
                        ApprovalContext.create().
                                withCurrentUser()
                                .withItem(source)
                );
        try {
            ScriptApproval.get().using(dsl, GroovyLanguage.get());
        } catch (RejectedAccessException e) {
            throw ScriptApproval.get().accessRejected(e, ApprovalContext.create().withItem(source));
        }
        return super.prepareGroovyCodeSource(dsl);
    }
}
