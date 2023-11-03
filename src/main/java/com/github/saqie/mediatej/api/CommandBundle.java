package com.github.saqie.mediatej.api;

import java.util.Optional;

@SuppressWarnings("unchecked")
public final class CommandBundle<T extends Command, R extends ErrorBuilder> {

    private final CommandHandler<T> commandHandler;
    private final CommandValidator<T, R> commandValidator;

    public CommandBundle(CommandHandler<T> commandHandler, CommandValidator<T, R> commandValidator) {
        this.commandHandler = commandHandler;
        this.commandValidator = commandValidator;
    }

    public CommandBundle(CommandValidator<? extends Command, ? extends ErrorBuilder> commandValidator, CommandHandler<? extends Command> commandHandler) {
        this.commandHandler = (CommandHandler<T>) commandHandler;
        this.commandValidator = (CommandValidator<T, R>) commandValidator;
    }

    public CommandBundle(CommandHandler<T> commandHandler) {
        this.commandHandler = commandHandler;
        this.commandValidator = null;
    }

    public CommandHandler<T> commandHandler() {
        return commandHandler;
    }

    public Optional<CommandValidator<T, R>> commandValidator() {
        return Optional.ofNullable(commandValidator);
    }
}
