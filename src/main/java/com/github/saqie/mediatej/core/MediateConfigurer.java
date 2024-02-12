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
        ClassKeyData keyData = MediateHelper.getKeyFromClass(commandHandler);
        checkHandlerConflicts(keyData);
        commandBundleMap.put(keyData.classKey(), new CommandBundle<>(commandHandler, null));
        return this;
    }

    public <T extends Command> MediateConfigurer register(List<CommandHandler<T>> commandHandlerList) {
        requireNotNullArgument(commandHandlerList, "Command handler list cannot be null");
        commandHandlerList.forEach(handler -> {
            ClassKeyData keyData = MediateHelper.getKeyFromClass(handler);
            checkHandlerConflicts(keyData);
            commandBundleMap.put(keyData.classKey(), new CommandBundle<>(handler, null));
        });
        return this;
    }

    public MediateConfigurer register(Set<CommandBundle<? extends Command, ? extends ErrorBuilder>> commandBundleList) {
        requireNotNullArgument(commandBundleList, "Command bundle set cannot be null");
        commandBundleList.forEach(commandBundle -> {
            ClassKeyData handlerClassKeyData = MediateHelper.getKeyFromClass(commandBundle.commandHandler());
            checkHandlerConflicts(handlerClassKeyData);
            commandBundle.commandValidator()
                    .ifPresent(validator -> {
                        ClassKeyData validatorClassKeyData = MediateHelper.getKeyFromClass(validator);
                        checkClassesKeysData(handlerClassKeyData, validatorClassKeyData, errorBuilder);
                    });
            commandBundleMap.put(handlerClassKeyData.classKey(), commandBundle);
        });
        return this;
    }

    public <T extends Command, R extends ErrorBuilder> MediateConfigurer register(List<CommandHandler<T>> commandHandlers, List<CommandValidator<T, R>> commandValidators) {
        requireNotNullArgument(commandHandlers, "Command handlers cannot be null");
        requireNotNullArgument(commandValidators, "Command validators cannot be null");

        Map<String, CommandHandler<T>> tempCommandHandlerMap = MediateHelper.resolveHandlers(commandHandlers);
        Map<String, CommandValidator<T, R>> tempCommandValidatorMap = MediateHelper.resolveValidators(commandValidators);

        scaleDownToCommandBundleMap(tempCommandHandlerMap, tempCommandValidatorMap);

        return this;
    }


    public <T extends Command, R extends ErrorBuilder> MediateConfigurer register(CommandHandler<T> commandHandler, CommandValidator<T, R> commandValidator) {
        requireNonNullArgument(commandHandler, "Command handler cannot be null");
        ClassKeyData handlerClassKeyData = MediateHelper.getKeyFromClass(commandHandler);
        checkHandlerConflicts(handlerClassKeyData);
        if (commandValidator != null) {
            ClassKeyData validatorClassKeyData = MediateHelper.getKeyFromClass(commandValidator);
            checkClassesKeysData(handlerClassKeyData, validatorClassKeyData, errorBuilder);
        }
        commandBundleMap.put(handlerClassKeyData.classKey(), new CommandBundle<>(commandHandler, commandValidator));
        return this;
    }


    public Mediate build() {
        return new Mediate(this);
    }

    void clear() {
        this.errorBuilder = null;
        this.commandBundleMap.clear();
    }

    Map<String, CommandBundle<? extends Command, ? extends ErrorBuilder>> commandBundleMap() {
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

    private void checkHandlerConflicts(ClassKeyData keyData) {
        if (HandlerConflictMode.THROW_EXCEPTION == handlerConflictMode) {
            if (commandBundleMap.containsKey(keyData.classKey())) {
                throw new MediateJConflictException("Handler for " + keyData.classKey() + " is already registered");
            }
        }
    }

    private <T extends Command, R extends ErrorBuilder> void scaleDownToCommandBundleMap(Map<String, CommandHandler<T>> tempCommandHandlerMap, Map<String, CommandValidator<T, R>> tempCommandValidatorMap) {
        for (String key : tempCommandHandlerMap.keySet()) {
            CommandHandler<T> commandHandler = tempCommandHandlerMap.get(key);
            if (tempCommandValidatorMap.containsKey(key)) {
                CommandValidator<T, R> commandValidator = tempCommandValidatorMap.get(key);
                checkClassesKeysData(commandHandler, commandValidator, errorBuilder);
                commandBundleMap.put(key, new CommandBundle<>(commandHandler, commandValidator));
            } else {
                commandBundleMap.put(key, new CommandBundle<>(commandHandler, null));
            }
        }
    }
}
