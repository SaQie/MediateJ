package com.github.saqie.mediatej.core;

import com.github.saqie.mediatej.api.*;
import com.github.saqie.mediatej.core.exception.MediateJMissingArgumentException;
import com.github.saqie.mediatej.core.exception.MediateJMissingHandlerException;
import com.github.saqie.mediatej.core.exception.MediateJMissingValidatorException;
import com.github.saqie.mediatej.core.exception.MediateJWrongParameterException;

import java.lang.reflect.Type;
import java.util.Collection;


final class Check {

    public static void requireNonNullArgument(Object o, String message) {
        if (o == null) {
            throw new MediateJMissingArgumentException(message);
        }
    }

    public static void requireNotNullArgument(Collection<?> c, String message) {
        if (c == null) {
            throw new MediateJMissingArgumentException(message);
        }
    }

    public static void requireCommandHandler(Command c, Object o) {
        if (o == null) {
            throw new MediateJMissingHandlerException("Command handler for " + c.getClass().getSimpleName() + " not found");
        }
    }

    public static <R extends ErrorBuilder, T extends Command> void checkValidatorParameter(CommandValidator<T, R> commandValidator, ErrorBuilder errorBuilder, String validatorName) {
        if (errorBuilder == null) {
            throw new MediateJMissingValidatorException("Error builder not provided ! use .registerErrorBuilder() to register a new error builder");
        }
        if (!validatorName.equals(errorBuilder.getClass().getCanonicalName())) {
            throw new MediateJWrongParameterException("Wrong error builder parameter type for validator " + commandValidator.getClass().getSimpleName());
        }
    }

    public static <R extends ErrorBuilder, T extends Command> void checkCommandParameter(String key, CommandValidator<T, R> commandValidator, String commandName) {
        if (!key.equals(commandName)) {
            throw new MediateJWrongParameterException("Wrong command parameter type for validator " + commandValidator.getClass().getSimpleName());
        }
    }

    public static void checkClassesKeysData(ClassKeyData handlerClassKeyData, ClassKeyData validatorClassKeyData, ErrorBuilder errorBuilder) {
        requireNotNullErrorBuilder(errorBuilder);
        requireCorrectErrorBuilderParameter(validatorClassKeyData, errorBuilder);
        requireCorrectFirstValidatorParameter(handlerClassKeyData, validatorClassKeyData);
    }


    public static void requireNotNullGenericParameterTypes(Type[] types, String targetClassName) {
        if (types == null || types.length < 1 || types[0].getTypeName().isEmpty()) {
            throw new MediateJMissingArgumentException("Class " + targetClassName + " doesn't have require generic command parameter");
        }
    }

    public static void requireNotNullErrorBuilder(ErrorBuilder errorBuilder) {
        if (errorBuilder == null) {
            throw new MediateJMissingValidatorException("Error builder not provided ! use .registerErrorBuilder() to register a new error builder");
        }
    }

    private static void requireCorrectErrorBuilderParameter(ClassKeyData validatorClassKeyData, ErrorBuilder errorBuilder) {
        if (!validatorClassKeyData.validatorKey().equals(errorBuilder.getClass().getCanonicalName())) {
            throw new MediateJWrongParameterException("Wrong error builder parameter type for validator " + validatorClassKeyData.validatorName());
        }
    }

    private static void requireCorrectFirstValidatorParameter(ClassKeyData handlerClassKeyData, ClassKeyData validatorClassKeyData) {
        if (!handlerClassKeyData.classKey().equals(validatorClassKeyData.classKey())) {
            throw new MediateJWrongParameterException("Wrong first parameter type for validator " + validatorClassKeyData.validatorName());
        }
    }


}
