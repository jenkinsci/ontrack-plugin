package net.nemerosa.ontrack.jenkins.dsl;

import groovy.lang.Binding;

public interface DSLLauncher {
    Object run(String dsl, Binding binding);
}
