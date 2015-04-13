package com.orders.inject;


import com.orders.matchers.MoreMatchers;
import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.orders.configuration.YamlConfigurationLoaderImpl;
import com.orders.configuration.spi.ConfigurationLoader;

public class DefaultConfigurationModule extends AbstractModule {

    @Override
    protected void configure() {
        bindListener(MoreMatchers.literalSubclassesOf(Configuration.class), new ConfigurationTypeListener());
        bind(ConfigurationLoader.class).to(YamlConfigurationLoaderImpl.class).in(Scopes.SINGLETON);
    }
}
