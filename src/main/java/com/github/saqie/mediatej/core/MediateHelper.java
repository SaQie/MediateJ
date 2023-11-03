package com.github.saqie.mediatej.core;

import com.github.saqie.mediatej.api.Command;
import com.github.saqie.mediatej.api.CommandHandler;
import com.github.saqie.mediatej.api.CommandValidator;
import com.github.saqie.mediatej.api.ErrorBuilder;
import com.github.saqie.mediatej.core.exception.MediateJMissingArgumentException;

import java.lang.reflect.Type;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.github.saqie.mediatej.core.Check.requireNotNullGenericParameterTypes;

final class MediateHelper {

    private static final Pattern PATTERN = Pattern.compile("<(.*?)>$");

    public static <T extends Command> String getGenericParameterNames(CommandHandler<T> commandHandler) {
        Type[] genericInterfaces = commandHandler.getClass().getGenericInterfaces();
        requireNotNullGenericParameterTypes(genericInterfaces, commandHandler.getClass().getSimpleName());
        String typeName = genericInterfaces[0].getTypeName();
        Matcher matcher = PATTERN.matcher(typeName);
        if (matcher.find()) {
            return matcher.group(1).replace('$', '.');
        }
        throw new MediateJMissingArgumentException("Handler " + commandHandler.getClass().getSimpleName() + " doesn't have required generic parameter type");
    }

    public static <T extends Command, R extends ErrorBuilder> String[] getGenericParameterNames(CommandValidator<T, R> commandValidator) {
        Type[] genericInterfaces = commandValidator.getClass().getGenericInterfaces();
        requireNotNullGenericParameterTypes(genericInterfaces, commandValidator.getClass().getSimpleName());
        String commandTypeName = genericInterfaces[0].getTypeName();
        Matcher matcher = PATTERN.matcher(commandTypeName);
        if (matcher.find()) {
            String group = matcher.group(1);
            return group.split(",");
        }
        throw new MediateJMissingArgumentException("Validator " + commandValidator.getClass().getSimpleName() + " doesn't have required generic parameter types");
    }

}
