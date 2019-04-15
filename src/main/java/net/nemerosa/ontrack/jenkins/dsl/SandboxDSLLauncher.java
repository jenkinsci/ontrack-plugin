package net.nemerosa.ontrack.jenkins.dsl;

import groovy.lang.GroovyShell;
import hudson.model.Item;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.jenkinsci.plugins.scriptsecurity.sandbox.RejectedAccessException;
import org.jenkinsci.plugins.scriptsecurity.sandbox.Whitelist;
import org.jenkinsci.plugins.scriptsecurity.sandbox.groovy.GroovySandbox;
import org.jenkinsci.plugins.scriptsecurity.sandbox.whitelists.ProxyWhitelist;
import org.jenkinsci.plugins.scriptsecurity.scripts.ApprovalContext;
import org.jenkinsci.plugins.scriptsecurity.scripts.ScriptApproval;

public class SandboxDSLLauncher extends AbstractDSLLauncher {

    private final Item source;

    public SandboxDSLLauncher(Item source) {
        this.source = source;
    }

    @Override
    protected CompilerConfiguration prepareCompilerConfiguration() {
        return GroovySandbox.createSecureCompilerConfiguration();
    }

    @Override
    protected ClassLoader prepareClassLoader(ClassLoader classLoader) {
        return GroovySandbox.createSecureClassLoader(classLoader);
    }

    @Override
    protected Object run(GroovyShell groovyShell, String script) {
        try {
            return GroovySandbox.run(groovyShell, script, new ProxyWhitelist(Whitelist.all(), new OntrackDSLWhitelist()));
        } catch (RejectedAccessException e) {
            throw new OntrackDSLException(
                    e.getMessage(),
                    ScriptApproval.get().accessRejected(e, ApprovalContext.create().withItem(source))
            );
        }
    }
}
