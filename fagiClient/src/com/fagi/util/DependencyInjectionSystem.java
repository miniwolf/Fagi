package com.fagi.util;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

public class DependencyInjectionSystem {
    private static Injector injector;
    private static Module module = new DefaultWiringModule();

    public static Injector getInstance() {
        if (injector == null) {
            injector = Guice.createInjector(module);
        }
        return injector;
    }

    public static void setModule(Module module) {
        DependencyInjectionSystem.module = module;
    }
}
