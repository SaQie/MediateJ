package com.github.saqie.mediatej.core;

import com.github.saqie.mediatej.api.*;
import com.github.saqie.mediatej.core.exception.MediateJMissingHandlerException;

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

    /**
     * Sends command to proper handler
     * Throws {@link MediateJMissingHandlerException} if the handler can't be found
     *
     * @param command -> Command instance to send
     */
    @Override
    public <T extends Command, R extends ErrorBuilder> void send(T command) {
        CommandBundle<T, R> commandBundle = bundleResolver.resolve(command);
        validatorResolver.run(command, commandBundle);
        commandBundle.commandHandler().handle(command);
    }
}
