package com.orders.servlet;


import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.inject.*;
import com.google.inject.servlet.GuiceServletContextListener;
import com.orders.DomainConstants;
import com.orders.collect.Registry;
import com.orders.inject.RegistryModule;
import com.orders.spi.Disposable;
import com.orders.spi.Worker;

import javax.servlet.ServletContextEvent;

import static com.orders.DomainConstants.DEFAULT_ENV;

public abstract class BaseServletContextListener extends GuiceServletContextListener {

    private final RegistryModule<Disposable> disposableRegistryModule = new RegistryModule<Disposable>(new TypeLiteral<Registry<Disposable>>(){});
    private final RegistryModule<Worker> workerRegistryModule = new RegistryModule<Worker>(new TypeLiteral<Registry<Worker>>(){});
    private Injector singleton = null;
    protected Stage stage;

    public BaseServletContextListener() {
        if("enabled".equals(DomainConstants.XFAST)) {
            stage = Stage.DEVELOPMENT;
        }
    }

    protected abstract Iterable<Module> getModules();

    @Override
    protected Injector getInjector() {
        if(null == singleton) {
            Iterable<Module> modules = Lists.<Module>newArrayList(disposableRegistryModule, workerRegistryModule);
            final Iterable<Module> extensions = getModules();
            if(null != extensions) {
                modules = Iterables.concat(modules, extensions);
            }
            final Stage appStage = null == this.stage ? DEFAULT_ENV.isStandaloneJvm() ? Stage.DEVELOPMENT : Stage.PRODUCTION : stage;
            singleton = Guice.createInjector(appStage, modules);
        }
        return singleton;
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        for(Disposable svc: disposableRegistryModule.get().getRegistered()) {
            svc.dispose();
        }
        super.contextDestroyed(servletContextEvent);
    }
}
