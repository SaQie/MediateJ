package com.github.saqie.mediatej.core;

import com.github.saqie.mediatej.api.*;
import com.github.saqie.mediatej.core.configuration.ErrorBuilderInstanceMode;
import com.github.saqie.mediatej.core.configuration.HandlerConflictMode;
import com.github.saqie.mediatej.core.exception.MediateJConflictException;

import static com.github.saqie.mediatej.core.Check.*;
import static com.github.saqie.mediatej.core.Check.requireNotNullArgument;

import java.util.*;

@SuppressWarnings({"UnusedReturnValue", "unused"})
public final class MediateConfigurer {

    private final Map<String, CommandBundle<? extends Command, ? extends ErrorBuilder>> commandBundleMap = new HashMap<>();
    private ErrorBuilder errorBuilder;

    private final HandlerConflictMode handlerConflictMode;
    private final ErrorBuilderInstanceMode errorBuilderInstanceMode;

    public MediateConfigurer() {
        MediateCoreConfigurer coreConfigurer = new MediateCoreConfigurer();
        this.errorBuilderInstanceMode = coreConfigurer.errorBuilderInstanceMode();
        this.handlerConflictMode = coreConfigurer.handlerConflictMode();
    }

    public MediateConfigurer(MediateCoreConfigurer coreConfigurer) {
        requireNonNullArgument(coreConfigurer, "Core configuration cannot be null");
        this.errorBuilderInstanceMode = coreConfigurer.errorBuilderInstanceMode();
        this.handlerConflictMode = coreConfigurer.handlerConflictMode();
    }

    public <T extends ErrorBuilder> MediateConfigurer registerErrorBuilder(T errorBuilder) {
        requireNonNullArgument(errorBuilder, "ErrorBuilder cannot be null");
        this.errorBuilder = errorBuilder;
        return this;
    }

    public <T extends Command> MediateConfigurer register(CommandHandler<T> commandHandler) {
        requireNonNullArgument(commandHandler, "Command handler cannot be null");
        String key = MediateHelper.getGenericParameterNames(commandHandler);
        checkHandlerConflicts(key);
        commandBundleMap.put(key, new CommandBundle<>(commandHandler, null));
        return this;
    }

    public <T extends Command> MediateConfigurer register(List<CommandHandler<T>> commandHandlerList) {
        requireNotNullArgument(commandHandlerList, "Command handler list cannot be null");
        commandHandlerList.forEach(handler -> {
            String key = MediateHelper.getGenericParameterNames(handler);
            checkHandlerConflicts(key);
            commandBundleMap.put(key, new CommandBundle<>(handler, null));
        });
        return this;
    }

    public MediateConfigurer register(Set<CommandBundle<? extends Command, ? extends ErrorBuilder>> commandBundleList) {
        requireNotNullArgument(commandBundleList, "Command bundle set cannot be null");
        commandBundleList.forEach(commandBundle -> {
            String key = MediateHelper.getGenericParameterNames(commandBundle.commandHandler());
            checkHandlerConflicts(key);
            commandBundle.commandValidator().ifPresent(validator -> checkGenericParameterTypes(key, errorBuilder, validator));
            commandBundleMap.put(key, commandBundle);
        });
        return this;
    }

    public <T extends Command, R extends ErrorBuilder> MediateConfigurer register(List<CommandHandler<T>> commandHandlers, List<CommandValidator<T, R>> commandValidators) {
        requireNotNullArgument(commandHandlers, "Command handlers cannot be null");
        requireNotNullArgument(commandValidators, "Command validators cannot be null");

        Map<String, CommandHandler<T>> tempCommandHandlerMap = new HashMap<>();
        Map<String, CommandValidator<T, R>> tempCommandValidatorMap = new HashMap<>();

        for (CommandHandler<T> commandHandler : commandHandlers) {
            String key = MediateHelper.getGenericParameterNames(commandHandler);
            tempCommandHandlerMap.put(key, commandHandler);
        }

        for (CommandValidator<T, R> commandValidator : commandValidators) {
            String[] genericParameterNames = MediateHelper.getGenericParameterNames(commandValidator);
            String key = genericParameterNames[0];
            tempCommandValidatorMap.put(key, commandValidator);
        }

        for (String key : tempCommandHandlerMap.keySet()) {
            CommandHandler<T> commandHandler = tempCommandHandlerMap.get(key);
            if (tempCommandValidatorMap.containsKey(key)) {
                CommandValidator<T, R> commandValidator = tempCommandValidatorMap.get(key);
                checkGenericParameterTypes(key, errorBuilder, commandValidator);
                commandBundleMap.put(key, new CommandBundle<>(commandHandler, commandValidator));
            } else {
                commandBundleMap.put(key, new CommandBundle<>(commandHandler, null));
            }
        }

        // TODO musisz sprawdzic czy nie podano walidatora ktory przypadkiem nie ma odpowiednika w command handlerze

        return this;
    }

    public <T extends Command, R extends ErrorBuilder> MediateConfigurer register(CommandHandler<T> commandHandler, CommandValidator<T, R> commandValidator) {
        requireNonNullArgument(commandHandler, "Command handler cannot be null");
        String key = MediateHelper.getGenericParameterNames(commandHandler);
        checkHandlerConflicts(key);
        if (commandValidator != null) {
            checkGenericParameterTypes(key, errorBuilder, commandValidator);
        }
        commandBundleMap.put(key, new CommandBundle<>(commandHandler, commandValidator));
        return this;
    }


    public Mediate build() {
        return new Mediate(this);
    }

    void clear() {
        this.errorBuilder = null;
        this.commandBundleMap.clear();
    }

    Map<String, CommandBundle<? extends Command, ? extends ErrorBuilder>> map() {
        return commandBundleMap;
    }

    ErrorBuilder errorBuilder() {
        return errorBuilder;
    }

    ErrorBuilderInstanceMode errorBuilderInstanceMode() {
        return errorBuilderInstanceMode;
    }

    HandlerConflictMode handlerConflictMode() {
        return handlerConflictMode;
    }

    private void checkHandlerConflicts(String key) {
        if (HandlerConflictMode.THROW_EXCEPTION == handlerConflictMode) {
            if (commandBundleMap.containsKey(key)) {
                throw new MediateJConflictException("Handler for " + key + " is already registered");
            }
        }
    }
}
