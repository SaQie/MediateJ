package com.github.saqie.mediatej.core;

import com.github.saqie.mediatej.api.Command;
import com.github.saqie.mediatej.api.CommandBundle;
import com.github.saqie.mediatej.api.ErrorBuilder;
import com.github.saqie.mediatej.api.MediateJ;

import static com.github.saqie.mediatej.core.Check.*;

import java.util.HashMap;
import java.util.Map;

public final class Mediate implements MediateJ {

    private final Map<String, CommandBundle<? extends Command, ? extends ErrorBuilder>> commandBundleMap = new HashMap<>();
    private final ValidatorResolver validatorResolver;

    public Mediate(MediateConfigurer configurer) {
        requireNonNullArgument(configurer, "Mediate configurer cannot be null");
        this.commandBundleMap.putAll(configurer.map());
        this.validatorResolver = new ValidatorResolver(configurer.errorBuilder(), configurer.errorBuilderInstanceMode());
        configurer.clear();
    }

    @SuppressWarnings("unchecked")
    public <T extends Command, R extends ErrorBuilder> void send(T command) {
        requireNonNullArgument(command, "Command cannot be null");
        CommandBundle<T, R> commandBundle = (CommandBundle<T, R>) commandBundleMap.get(command.getClass().getCanonicalName());
        requireCommandHandler(command, commandBundle);
        validatorResolver.run(command, commandBundle);
        commandBundle.commandHandler().handle(command);
    }


}
