package com.orders.map;


import org.codehaus.jackson.Version;
import org.codehaus.jackson.map.module.SimpleModule;

public class MixinMappingModule extends SimpleModule {

    static final Version V1 = new Version(1, 0, 0, null);

    protected MixinMappingModule() {
        super(null, null);
    }

    @Override
    public String getModuleName() {
        return getClass().getCanonicalName();
    }

    @Override
    public Version version() {
        return V1;
    }
}
