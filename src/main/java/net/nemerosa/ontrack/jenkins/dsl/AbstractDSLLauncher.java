package net.nemerosa.ontrack.jenkins.dsl;

import groovy.lang.Binding;
import groovy.lang.GroovyCodeSource;
import groovy.lang.GroovyShell;
import org.codehaus.groovy.control.CompilerConfiguration;

import java.util.Collections;

import static groovy.lang.GroovyShell.DEFAULT_CODE_BASE;

public abstract class AbstractDSLLauncher implements DSLLauncher {

    @Override
    public Object run(String dsl, Binding binding) {
        CompilerConfiguration compilerConfiguration = prepareCompilerConfiguration();
        ClassLoader classLoader = prepareClassLoader(AbstractDSLLauncher.class.getClassLoader());

        // Groovy shell
        GroovyShell shell = new GroovyShell(
                classLoader,
                binding,
                compilerConfiguration
        );

        // Runs the script
        return run(shell, dsl);
    }

    protected Object run(GroovyShell groovyShell, String script) {
        return groovyShell.run(
                prepareGroovyCodeSource(script),
                Collections.emptyList()
        );
    }

    protected GroovyCodeSource prepareGroovyCodeSource(String dsl) {
        return new GroovyCodeSource(dsl, "script", DEFAULT_CODE_BASE);
    }

    protected ClassLoader prepareClassLoader(ClassLoader classLoader) {
        return classLoader;
    }

    protected CompilerConfiguration prepareCompilerConfiguration() {
        return new CompilerConfiguration(CompilerConfiguration.DEFAULT);
    }
}
