package net.nemerosa.ontrack.jenkins.extension;

import javaposse.jobdsl.dsl.Context;
import org.apache.commons.lang.StringUtils;

public class OntrackChoiceParameterContext implements Context {

    private String name;
    private String description = "";
    private String dsl;
    private boolean sandbox = false;
    private String valueProperty = "name";

    public void name(String value) {
        setName(value);
    }

    public void description(String value) {
        setDescription(value);
    }

    public void dsl(String value) {
        setDsl(value);
    }

    public void sandbox() {
        setSandbox(true);
    }

    public void valueProperty(String value) {
        setValueProperty(value);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDsl() {
        return dsl;
    }

    public void setDsl(String dsl) {
        this.dsl = dsl;
    }

    public boolean isSandbox() {
        return sandbox;
    }

    public void setSandbox(boolean sandbox) {
        this.sandbox = sandbox;
    }

    public String getValueProperty() {
        return valueProperty;
    }

    public void setValueProperty(String valueProperty) {
        this.valueProperty = valueProperty;
    }

    public void validate() {
        if (StringUtils.isBlank(name)) {
            throw new IllegalStateException("`name` is required.");
        }
        if (StringUtils.isBlank(dsl)) {
            throw new IllegalStateException("`dsl` is required.");
        }
    }
}
