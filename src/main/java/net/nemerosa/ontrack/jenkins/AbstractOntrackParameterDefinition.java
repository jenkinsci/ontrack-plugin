package net.nemerosa.ontrack.jenkins;

import hudson.model.ParameterDefinition;
import net.nemerosa.ontrack.jenkins.dsl.DSLRunner;
import net.nemerosa.ontrack.jenkins.dsl.OntrackDSLRunner;
import org.apache.commons.beanutils.BeanUtils;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

public abstract class AbstractOntrackParameterDefinition extends ParameterDefinition {

    private final String dsl;
    private final boolean sandbox;
    private final String valueProperty;
    private final transient DSLRunner dslRunner;
    private final String injectProperties;
    private final Map<String, Object> bindings;

    public AbstractOntrackParameterDefinition(String name, String description, String dsl, boolean sandbox, String valueProperty, String injectProperties) {
        this(
                name,
                description,
                dsl,
                sandbox,
                valueProperty,
                null,
                injectProperties,
                Collections.emptyMap()
        );
    }

    public AbstractOntrackParameterDefinition(String name, String description, String dsl, boolean sandbox, String valueProperty, Map<String, Object> bindings) {
        this(
                name,
                description,
                dsl,
                sandbox,
                valueProperty,
                null,
                null,
                bindings
        );
    }

    public AbstractOntrackParameterDefinition(String name, String description, String dsl, boolean sandbox, String valueProperty, DSLRunner dslRunner, String injectProperties, Map<String, Object> bindings) {
        super(name, description);
        this.dsl = dsl;
        this.valueProperty = valueProperty;
        this.dslRunner = dslRunner;
        this.sandbox = sandbox;
        this.injectProperties = injectProperties;
        this.bindings = bindings;
    }

    public String getDsl() {
        return dsl;
    }

    public String getValueProperty() {
        return valueProperty;
    }

    public boolean isSandbox() {
        return sandbox;
    }

    public String getInjectProperties() {
        return injectProperties;
    }

    public Map<String, Object> getBindings() {
        return bindings;
    }

    protected Object runDSL() {
        DSLRunner runner = dslRunner != null ? dslRunner : createDSLRunner();
        return runner.run(dsl);
    }

    protected DSLRunner createDSLRunner() {
        try {
            OntrackDSLRunner runner = OntrackDSLRunner.getRunner().setSandbox(sandbox).injectProperties(injectProperties, null, null);
            if (bindings != null) {
                for (Map.Entry<String, Object> entry : bindings.entrySet()) {
                    runner = runner.addBinding(entry.getKey(), entry.getValue());
                }
            }
            return runner;
        } catch (IOException | InterruptedException e) {
            throw new IllegalStateException("Cannot create the DSL runner", e);
        }
    }

    protected String getProperty(Object any, String property) {
        try {
            return BeanUtils.getProperty(any, property);
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    String.format(
                            "Could not get property %s on object %s",
                            property,
                            any
                    )
            );
        }
    }

}
