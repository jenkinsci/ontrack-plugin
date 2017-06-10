package net.nemerosa.ontrack.jenkins.dsl;

import groovy.lang.Binding;
import groovy.lang.GroovyCodeSource;
import groovy.lang.GroovyShell;
import groovy.lang.Script;
import org.codehaus.groovy.control.CompilerConfiguration;

import static groovy.lang.GroovyShell.DEFAULT_CODE_BASE;

public abstract class AbstractDSLLauncher implements DSLLauncher {

    @Override
    public Object run(String dsl, Binding binding) {
        CompilerConfiguration compilerConfiguration = prepareCompilerConfiguration();
        ClassLoader classLoader = prepareClassLoader(AbstractDSLLauncher.class.getClassLoader());
        GroovyCodeSource groovyCodeSource = prepareGroovyCodeSource(dsl);

        // Groovy shell
        GroovyShell shell = new GroovyShell(
                classLoader,
                new Binding(),
                compilerConfiguration
        );

        // Groovy script
        Script groovyScript = shell.parse(groovyCodeSource);

        // Binding
        groovyScript.setBinding(binding);

        // Runs the script
        return run(groovyScript);
    }

    protected Object run(Script groovyScript) {
        return groovyScript.run();
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
