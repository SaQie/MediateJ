package com.github.saqie.mediatej.core;

import com.github.saqie.mediatej.api.*;
import com.github.saqie.mediatej.core.configuration.ErrorBuilderInstanceMode;
import com.github.saqie.mediatej.core.exception.MediateJConflictException;

import java.lang.reflect.InvocationTargetException;

@SuppressWarnings("unchecked")
final class ValidatorResolver {

    private final ErrorBuilder errorBuilder;
    private final ErrorBuilderInstanceMode instanceMode;

    public ValidatorResolver(MediateConfigurer configurer) {
        this.errorBuilder = configurer.errorBuilder();
        this.instanceMode = configurer.errorBuilderInstanceMode();
    }

    <T extends Command, R extends ErrorBuilder> void run(T command, CommandBundle<T, R> commandBundle) {
        commandBundle.commandValidator().ifPresent((validator) -> {
            ErrorBuilder builder = getErrorBuilderInstance();
            validator.validate(command, (R) builder);
            builder.build();
        });
    }

    private ErrorBuilder getErrorBuilderInstance() {
        if (ErrorBuilderInstanceMode.PER_SEND == instanceMode) {
            return getInstance();
        }
        return errorBuilder;
    }

    private ErrorBuilder getInstance() {
        try {
            return errorBuilder.getClass().getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            throw new MediateJConflictException("Cannot create instance for class " + errorBuilder.getClass().getCanonicalName()
                    + " make sure that class have no args constructor", e);
        }
    }

}
