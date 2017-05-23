package net.nemerosa.ontrack.jenkins.dsl;

import groovy.lang.GroovyCodeSource;
import org.jenkinsci.plugins.scriptsecurity.scripts.ScriptApproval;
import org.jenkinsci.plugins.scriptsecurity.scripts.UnapprovedUsageException;
import org.jenkinsci.plugins.scriptsecurity.scripts.languages.GroovyLanguage;

public class ApprovalBasedDSLLauncher extends AbstractDSLLauncher {
    @Override
    protected GroovyCodeSource prepareGroovyCodeSource(String dsl) {
        try {
            ScriptApproval.get().using(dsl, GroovyLanguage.get());
        } catch (UnapprovedUsageException e) {
            throw new OntrackDSLException(e);
        }
        return super.prepareGroovyCodeSource(dsl);
    }
}
