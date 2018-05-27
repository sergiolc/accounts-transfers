package com.slcorrea.accounts;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.slcorrea.accounts.resources.TransactionResource;
import io.dropwizard.Application;
import io.dropwizard.setup.Environment;

import com.slcorrea.accounts.resources.AccountResource;

public class App extends Application<AppConfig> {

    public static void main(String[] args) throws Exception {
        new App().run(args);
    }

    public void run(AppConfig config, Environment environment) {
        Injector injector = Guice.createInjector();
        environment.jersey().register(injector.getInstance(AccountResource.class));
        environment.jersey().register(injector.getInstance(TransactionResource.class));
    }
}