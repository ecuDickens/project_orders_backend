package com.orders.servlet;

import com.google.inject.AbstractModule;
import com.orders.web.AccountResource;

public class APIModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(AccountResource.class);
    }
}
