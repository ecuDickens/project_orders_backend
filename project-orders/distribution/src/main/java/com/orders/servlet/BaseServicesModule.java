package com.orders.servlet;

import com.orders.inject.DefaultConfigurationModule;
import com.orders.inject.DefaultEnvironmentModule;
import com.orders.jpa.JpaEntityManagerServiceImpl;
import com.orders.jpa.JpaServiceConstants;
import com.orders.jpa.spi.JpaEntityManagerService;
import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.name.Names;
import com.orders.service.threadpool.ThreadPoolServiceImpl;
import com.orders.spi.ThreadPoolService;

/**
 * Module which defines all Service injections
 */
public class BaseServicesModule extends AbstractModule {

    @Override
    protected void configure() {
        install(new DefaultEnvironmentModule());
        install(new DefaultConfigurationModule());
        bind(JpaEntityManagerService.class)
                .to(JpaEntityManagerServiceImpl.class)
                .in(Singleton.class);
        Multibinder.newSetBinder(binder(), String.class, Names.named(JpaServiceConstants.ENTITY_CLASS_NAMES));
        bind(ThreadPoolService.class)
                .to(ThreadPoolServiceImpl.class)
                .in(Singleton.class);
    }
}
