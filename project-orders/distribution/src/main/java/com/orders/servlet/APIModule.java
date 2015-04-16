package com.orders.servlet;

import com.google.inject.AbstractModule;
import com.orders.web.AccountResource;
import com.orders.web.InvoiceResource;
import com.orders.web.OrderResource;
import com.orders.web.ProductResource;

public class APIModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(AccountResource.class);
        bind(OrderResource.class);
        bind(InvoiceResource.class);
        bind(ProductResource.class);
    }
}
