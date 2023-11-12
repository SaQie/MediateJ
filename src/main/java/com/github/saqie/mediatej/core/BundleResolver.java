package com.github.saqie.mediatej.core;

import com.github.saqie.mediatej.api.*;

import java.util.HashMap;
import java.util.Map;

import static com.github.saqie.mediatej.core.Check.*;

final class BundleResolver {

    private final Map<String, CommandBundle<? extends Command, ? extends ErrorBuilder>> commandBundleMap = new HashMap<>();
    private final Map<String, RequestBundle<? extends Request, ? extends ErrorBuilder, ?>> requestBundleMap = new HashMap<>();

    public BundleResolver(MediateConfigurer configurer) {
        this.commandBundleMap.putAll(configurer.commandBundleMap());
        this.requestBundleMap.putAll(configurer.requestBundleMap());
    }

    @SuppressWarnings("unchecked")
    <T extends Command, R extends ErrorBuilder> CommandBundle<T, R> resolve(T command) {
        requireNonNullArgument(command, "Command cannot be null");
        CommandBundle<T, R> commandBundle = (CommandBundle<T, R>) commandBundleMap.get(command.getClass().getCanonicalName());
        requireCommandHandler(command, commandBundle);
        return commandBundle;
    }

    @SuppressWarnings("unchecked")
    <T extends Request<E>, R extends ErrorBuilder, E> RequestBundle<T, R, E> resolve(T request) {
        requireNonNullArgument(request, "Request cannot be null");
        RequestBundle<T, R, E> requestBundle = (RequestBundle<T, R, E>) requestBundleMap.get(request.getClass().getCanonicalName());
        requireRequestHandler(request, requestBundle);
        return requestBundle;
    }
}
