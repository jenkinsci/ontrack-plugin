package net.nemerosa.ontrack.jenkins.extension;

import javaposse.jobdsl.dsl.Context;
import org.apache.commons.lang.StringUtils;

public class OntrackBuildParameterContext implements Context {

    private String name;
    private String description = "";
    private String project;
    private String branch;
    private String promotion;
    private boolean useLabel;
    private int count = 10;

    public void name(String value) {
        setName(value);
    }

    public void description(String value) {
        setDescription(value);
    }

    public void project(String value) {
        setProject(value);
    }

    public void branch(String value) {
        setBranch(value);
    }

    public void promotion(String value) {
        setPromotion(value);
    }

    public void useLabel() {
        setUseLabel(true);
    }

    public void count(int value) {
        setCount(value);
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

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getPromotion() {
        return promotion;
    }

    public void setPromotion(String promotion) {
        this.promotion = promotion;
    }

    public boolean isUseLabel() {
        return useLabel;
    }

    public void setUseLabel(boolean useLabel) {
        this.useLabel = useLabel;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void validate() {
        if (StringUtils.isBlank(name)) {
            throw new IllegalStateException("`name` is required.");
        }
        if (StringUtils.isBlank(project)) {
            throw new IllegalStateException("`project` is required.");
        }
        if (StringUtils.isBlank(branch)) {
            throw new IllegalStateException("`branch` is required.");
        }
        if (count <= 0) {
            throw new IllegalStateException("`count` must be >= 0.");
        }
    }
}
