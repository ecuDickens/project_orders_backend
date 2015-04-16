package com.orders.servlet;

import com.google.inject.AbstractModule;
import com.orders.web.*;

public class APIModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(AccountResource.class);
        bind(OrderResource.class);
        bind(InvoiceResource.class);
        bind(ProductResource.class);
        bind(GeneratorResource.class);
    }
}
