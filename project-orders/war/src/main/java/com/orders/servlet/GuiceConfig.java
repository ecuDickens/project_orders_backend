package com.orders.servlet;

import com.orders.DomainConstants;
import com.orders.collect.Registry;
import com.orders.inject.RegistryModule;
import com.orders.logging.Marker;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;
import com.orders.service.ExternalServices;
import com.orders.spi.ExternalService;
import com.orders.spi.Worker;
import com.yammer.metrics.HealthChecks;
import com.yammer.metrics.core.HealthCheck;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

import javax.servlet.ServletContextEvent;
import java.util.List;

/**
 * Guice ConfigurationLoader
 */
public class GuiceConfig extends BaseServletContextListener {

    private static final XLogger LOGGER = XLoggerFactory.getXLogger(GuiceConfig.class);

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        // These properties should only be set in a true external web container, NOT in a test container
        // These properties MUST be set BEFORE initializing the context
        System.setProperty("com.orders.root", servletContextEvent.getServletContext().getRealPath("/"));
        System.setProperty("com.orders.standalone", "false");
        LOGGER.info("Initialized system properties.");

        // This initializes the whole app
        super.contextInitialized(servletContextEvent);

        // start all Workers
        final Registry<Worker> workerRegistry = getInjector().getInstance(Key.get(new TypeLiteral<Registry<Worker>>() {
        }));
        final ImmutableSet<Worker> workers = workerRegistry.getRegistered();
        final List<ListenableFuture<Boolean>> futures = Lists.newArrayListWithExpectedSize(workers.size());
        if(!"enabled".equals(DomainConstants.XFAST)) {
            for(Worker instance : workers) {
                futures.add(instance.start());
            }
        }

        Futures.addCallback(Futures.allAsList(futures), new FutureCallback<List<Boolean>>() {
            @Override
            public void onSuccess(List<Boolean> result) {
                LOGGER.info("All workers are running.");
            }

            @Override
            public void onFailure(Throwable t) {
                LOGGER.error(Marker.insert(Marker.ORDERS_ERROR_INTERNAL, t.getMessage()), t);
            }
        });

        registerHealthChecks();

        servletContextEvent.getServletContext().setAttribute("com.orders.injector", getInjector());

        LOGGER.info("Orders servlet has been initialized");
    }

    private void registerHealthChecks() {
        final Registry<ExternalService> services = getInjector().getInstance(Key.get(new TypeLiteral<Registry<ExternalService>>() {}));

        for(final ExternalService service : services.getRegistered()) {
            HealthChecks.register(new HealthCheck(ExternalServices.getName(service)) {

                @Override
                protected Result check() throws Exception {
                    try {
                        service.assertHealthy();
                        return Result.healthy();
                    }
                    catch(Exception e){
                        return Result.unhealthy(e.getMessage());
                    }
                }
            });
        }
    }

    @Override
    protected Iterable<Module> getModules() {
        return Lists.<Module>newArrayList(
                new BaseServicesModule(),
                new JerseyServletModule(),
                new APIModule(),
                new InstrumentationModule(),
                new RegistryModule<ExternalService>(new TypeLiteral<Registry<ExternalService>>(){}));
    }
}
