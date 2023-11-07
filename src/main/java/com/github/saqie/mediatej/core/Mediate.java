package com.github.saqie.mediatej.core;

import com.github.saqie.mediatej.api.*;

import static com.github.saqie.mediatej.core.Check.*;

public final class Mediate implements MediateJ {

    private final ValidatorResolver validatorResolver;
    private final BundleResolver bundleResolver;

    public Mediate(MediateConfigurer configurer) {
        requireNonNullArgument(configurer, "Mediate configurer cannot be null");
        this.bundleResolver = new BundleResolver(configurer);
        this.validatorResolver = new ValidatorResolver(configurer);
        configurer.clear();
    }

    @Override
    public <T extends Command, R extends ErrorBuilder> void send(T command) {
        CommandBundle<T, R> commandBundle = bundleResolver.resolve(command);
        validatorResolver.run(command, commandBundle);
        commandBundle.commandHandler().handle(command);
    }

    @Override
    public <T extends Request, R extends ErrorBuilder, E> E send(T request) {
        RequestBundle<T, R, E> requestBundle = bundleResolver.resolve(request);
        validatorResolver.run(request, requestBundle);
        return requestBundle.requestHandler().handle(request);
    }
}
