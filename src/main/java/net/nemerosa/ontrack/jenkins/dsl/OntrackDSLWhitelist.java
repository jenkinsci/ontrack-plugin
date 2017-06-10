package net.nemerosa.ontrack.jenkins.dsl;

import org.jenkinsci.plugins.scriptsecurity.sandbox.whitelists.AbstractWhitelist;

import java.lang.reflect.Method;

public class OntrackDSLWhitelist extends AbstractWhitelist {
    @Override
    public boolean permitsMethod(Method method, Object receiver, Object[] args) {
        return method.getDeclaringClass().getPackage().getName().startsWith("net.nemerosa.ontrack.dsl");
    }
}
