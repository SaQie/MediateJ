package com.github.saqie.mediatej.core;

import com.github.saqie.mediatej.api.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

final class MediateHelper {

    public static <T extends Request<R>, R> ClassKeyData getKeyFromClass(RequestHandler<T, R> requestHandler) {
        return new ClassKeyData(requestHandler);
    }

    public static <T extends Request<E>, R extends ErrorBuilder, E> ClassKeyData getKeyFromClass(RequestValidator<T, R> requestValidator) {
        return new ClassKeyData(requestValidator);
    }

    public static <T extends Command, R extends ErrorBuilder> ClassKeyData getKeyFromClass(CommandValidator<T, R> commandValidator) {
        return new ClassKeyData(commandValidator);
    }

    public static <T extends Command> ClassKeyData getKeyFromClass(CommandHandler<T> commandHandler) {
        return new ClassKeyData(commandHandler);
    }


    public static <T extends Command, R extends ErrorBuilder> Map<String, CommandValidator<T, R>> resolveValidators(List<CommandValidator<T, R>> commandValidators) {
        Map<String, CommandValidator<T, R>> tempCommandValidatorMap = new HashMap<>();
        for (CommandValidator<T, R> commandValidator : commandValidators) {
            ClassKeyData keyData = MediateHelper.getKeyFromClass(commandValidator);
            tempCommandValidatorMap.put(keyData.classKey(), commandValidator);
        }
        return tempCommandValidatorMap;
    }

    public static <T extends Command> Map<String, CommandHandler<T>> resolveHandlers(List<CommandHandler<T>> commandHandlers) {
        Map<String, CommandHandler<T>> tempCommandHandlerMap = new HashMap<>();
        for (CommandHandler<T> commandHandler : commandHandlers) {
            ClassKeyData keyData = MediateHelper.getKeyFromClass(commandHandler);
            tempCommandHandlerMap.put(keyData.classKey(), commandHandler);
        }
        return tempCommandHandlerMap;
    }

}
