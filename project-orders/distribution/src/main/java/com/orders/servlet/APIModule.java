package com.orders.servlet;

import com.google.inject.AbstractModule;
import com.orders.web.AccountResource;
import com.orders.web.OrderResource;

public class APIModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(AccountResource.class);
        bind(OrderResource.class);
    }
}
