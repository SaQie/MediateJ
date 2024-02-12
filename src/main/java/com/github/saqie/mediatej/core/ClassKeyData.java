package com.github.saqie.mediatej.core;

import com.github.saqie.mediatej.api.CommandHandler;
import com.github.saqie.mediatej.api.CommandValidator;
import com.github.saqie.mediatej.core.exception.MediateJMissingArgumentException;

import java.lang.reflect.Type;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.github.saqie.mediatej.core.Check.requireNotNullGenericParameterTypes;

@SuppressWarnings("rawtypes")
class ClassKeyData {

    private static final Pattern PATTERN = Pattern.compile("<(.*?)>$");

    private String classKey;
    private String validatorKey;
    private final String className;
    private final String validatorName;


    public ClassKeyData(CommandHandler commandHandler) {
        extractClassKeyData(commandHandler.getClass().getGenericInterfaces(), commandHandler.getClass().getSimpleName());
        this.className = commandHandler.getClass().getSimpleName();
        this.validatorName = "";
    }

    public ClassKeyData(CommandValidator commandValidator) {
        extractClassKeyData(commandValidator.getClass().getGenericInterfaces(), commandValidator.getClass().getSimpleName());
        this.validatorName = commandValidator.getClass().getSimpleName();
        this.className = "";
    }

    private void extractClassKeyData(Type[] genericInterfaces, String classSimpleName) {
        requireNotNullGenericParameterTypes(genericInterfaces, classSimpleName);
        String commandTypeName = genericInterfaces[0].getTypeName();
        Matcher matcher = PATTERN.matcher(commandTypeName);
        if (matcher.find()) {
            String group = matcher.group(1);
            String[] split = group.split(",");
            if (split.length > 1) {
                this.classKey = split[0].trim().replace('$', '.');
                this.validatorKey = split[1].trim().replace('$', '.');
            } else {
                this.classKey = group.replace('$', '.');
                this.validatorKey = "";
            }
            return;
        }
        throw new MediateJMissingArgumentException("Class " + classSimpleName + " doesn't have required generic interface parameters");
    }


    public String classKey() {
        return classKey;
    }

    public String validatorKey() {
        return validatorKey;
    }

    public String className() {
        return className;
    }

    public String validatorName() {
        return validatorName;
    }
}
