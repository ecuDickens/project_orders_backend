package com.orders.inject;


import com.google.inject.AbstractModule;
import com.orders.DomainConstants;
import com.orders.Env;

public class DefaultEnvironmentModule extends AbstractModule {

    private String override = null;

    @Override
    protected void configure() {
        Env env = DomainConstants.DEFAULT_ENV;
        if(null != override) {
            env = env.withEnvironment(override);
        }
        bind(Env.class).toInstance(env);
    }
}
