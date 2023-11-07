package com.github.saqie.mediatej.core;

import com.github.saqie.mediatej.api.*;
import com.github.saqie.mediatej.core.configuration.ErrorBuilderInstanceMode;
import com.github.saqie.mediatej.core.configuration.HandlerConflictMode;
import com.github.saqie.mediatej.core.exception.MediateJConflictException;
import com.github.saqie.mediatej.core.exception.MediateJMissingArgumentException;
import com.github.saqie.mediatej.core.exception.MediateJWrongParameterException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class MediateConfigurerTest {


    @Test
    @DisplayName("Should set default parameters values when create mediate configurer via constructor")
    public void shouldSetDefaultParameterValuesWhenCreateMediateConfigurer() {
        // given
        // when
        MediateConfigurer configurer = new MediateConfigurer();

        // then
        assertEquals(HandlerConflictMode.OVERRIDE, configurer.handlerConflictMode());
        assertEquals(ErrorBuilderInstanceMode.ONE, configurer.errorBuilderInstanceMode());
        assertNotNull(configurer.commandBundleMap());
        assertNull(configurer.errorBuilder());
    }

    @Test
    @DisplayName("Should set parameters from provided core configurer")
    public void shouldSetParameterValuesFromCoreConfigurer() {
        // given
        MediateCoreConfigurer coreConfigurer = new MediateCoreConfigurer();
        coreConfigurer.errorBuilderInstanceMode(ErrorBuilderInstanceMode.PER_SEND)
                .handlerConflictMode(HandlerConflictMode.OVERRIDE);

        // when
        MediateConfigurer configurer = new MediateConfigurer(coreConfigurer);

        // then
        assertEquals(HandlerConflictMode.OVERRIDE, configurer.handlerConflictMode());
        assertEquals(ErrorBuilderInstanceMode.PER_SEND, configurer.errorBuilderInstanceMode());
        assertNotNull(configurer.commandBundleMap());
        assertNull(configurer.errorBuilder());
    }

    @Test
    @DisplayName("Should throw MediateJMissingArgumentException if provided core configurer is null")
    public void shouldThrowExceptionIfProvidedCoreConfigurerIsNull() {
        // given
        MediateCoreConfigurer coreConfigurer = null;

        // when
        // then
        MediateJMissingArgumentException exception = assertThrowsExactly(MediateJMissingArgumentException.class, () -> new MediateConfigurer(coreConfigurer));
        assertEquals("Core configuration cannot be null", exception.getMessage());

    }

    @Test
    @DisplayName("Should throw MediateJMissingArgumentException if provided error builder is null")
    public void shouldThrowExceptionIfRegisterErrorBuilderWithNullArgument() {
        // given
        MediateConfigurer configurer = new MediateConfigurer();
        ErrorBuilder errorBuilder = null;

        // when
        // then
        MediateJMissingArgumentException exception = assertThrowsExactly(MediateJMissingArgumentException.class, () -> configurer.registerErrorBuilder(errorBuilder));
        assertEquals("ErrorBuilder cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Should register new error builder")
    public void shouldRegisterErrorBuilder() {
        // given
        MediateTestClassPack.TestErrorBuilder errorBuilder = new MediateTestClassPack.TestErrorBuilder();
        MediateConfigurer configurer = new MediateConfigurer();

        // when
        configurer.registerErrorBuilder(errorBuilder);

        // then
        assertEquals(errorBuilder, configurer.errorBuilder());
    }

    @Test
    @DisplayName("Should register new command handler")
    public void shouldRegisterNewCommandHandler() {
        // given
        MediateTestClassPack.TestCommandHandler testCommandHandler = new MediateTestClassPack.TestCommandHandler();
        MediateConfigurer configurer = new MediateConfigurer();

        // when
        configurer.register(testCommandHandler);

        // then
        assertEquals(1, configurer.commandBundleMap().size());
        assertEquals(testCommandHandler, configurer.commandBundleMap().values().iterator().next().commandHandler());
        assertEquals(MediateTestClassPack.TestCommand.class.getCanonicalName(), configurer.commandBundleMap().keySet().iterator().next());
        assertFalse(configurer.commandBundleMap().values().iterator().next().commandValidator().isPresent());
    }

    @Test
    @DisplayName("Should throw MediateJMissingArgumentException if provided command handler is null")
    public void shouldThrowExceptionIfProvidedCommandHandlerIsNull() {
        // given
        MediateTestClassPack.TestCommandHandler testCommandHandler = null;
        MediateConfigurer configurer = new MediateConfigurer();

        // when
        // then
        assertThrowsExactly(MediateJMissingArgumentException.class, () -> configurer.register(testCommandHandler),
                "Command handler cannot be null");
    }

    @Test
    @DisplayName("Should not throw exception if handler is already registered and handler conflict mode is set to override in configurer")
    public void shouldOverrideCommandHandlerIfHandlerAlreadyRegisteredAndHandlerConflictModeIsSetToDoNothing() {
        // given
        MediateTestClassPack.TestCommandHandler testCommandHandler = new MediateTestClassPack.TestCommandHandler();
        MediateTestClassPack.TestCommandHandlerSecond testCommandHandlerSecond = new MediateTestClassPack.TestCommandHandlerSecond();
        MediateConfigurer configurer = new MediateCoreConfigurer()
                .handlerConflictMode(HandlerConflictMode.OVERRIDE)
                .build();

        // First register
        configurer.register(testCommandHandler);

        // when
        configurer.register(testCommandHandlerSecond);

        // then
        assertEquals(1, configurer.commandBundleMap().size());
        assertEquals(testCommandHandlerSecond, configurer.commandBundleMap().values().iterator().next().commandHandler());
        assertFalse(configurer.commandBundleMap().values().iterator().next().commandValidator().isPresent());
        assertEquals(MediateTestClassPack.TestCommand.class.getCanonicalName(), configurer.commandBundleMap().keySet().iterator().next());
    }

    @Test
    @DisplayName("Should throw MediateJConflictException if given command handler is already registered")
    public void shouldThrowExceptionIfHandlerAlreadyRegisteredAndHandlerConflictModeIsSetToThrowException() {
        // given
        MediateTestClassPack.TestCommandHandler testCommandHandler = new MediateTestClassPack.TestCommandHandler();
        MediateConfigurer configurer = new MediateCoreConfigurer()
                .handlerConflictMode(HandlerConflictMode.THROW_EXCEPTION)
                .build();

        // First register
        configurer.register(testCommandHandler);

        // when
        // then
        MediateJConflictException exception = assertThrowsExactly(MediateJConflictException.class, () -> configurer.register(testCommandHandler));
        assertEquals("Handler for " + MediateTestClassPack.TestCommand.class.getCanonicalName() + " is already registered", exception.getMessage());

    }


    @Test
    @DisplayName("Should throw MediateJMissingArgumentException if given command handler list is null")
    public void shouldThrowExceptionIfProvidedCommandHandlerListIsNull() {
        // given
        List<CommandHandler<Command>> handlers = null;
        MediateConfigurer configurer = new MediateConfigurer();

        // when
        // then
        MediateJMissingArgumentException exception = assertThrowsExactly(MediateJMissingArgumentException.class, () -> configurer.register(handlers));
        assertEquals("Command handler list cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw MediateJMissingArgumentException if given command handler list is null")
    public void shouldThrowExceptionIfProvidedCommandBundleListIsNull() {
        // given
        Set<CommandBundle<?, ?>> commandBundles = null;
        MediateConfigurer configurer = new MediateConfigurer();

        // when
        // then
        MediateJMissingArgumentException exception = assertThrowsExactly(MediateJMissingArgumentException.class, () -> configurer.register(commandBundles));
        assertEquals("Command bundle set cannot be null", exception.getMessage());
    }


    @Test
    @DisplayName("Should throw MediateJMissingArgumentException if given command handler list is null")
    public void shouldThrowExceptionIfCommandHandlerListIsNull() {
        // given
        List<CommandHandler<Command>> commandHandlers = null;
        List<CommandValidator<Command, ErrorBuilder>> commandValidators = Collections.emptyList();

        MediateConfigurer configurer = new MediateConfigurer();

        // when
        // then
        MediateJMissingArgumentException exception = assertThrowsExactly(MediateJMissingArgumentException.class, () -> configurer.register(commandHandlers, commandValidators));
        assertEquals("Command handlers cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw MediateJMissingArgumentException if given command handler list is null")
    public void shouldThrowExceptionIfCommandValidatorListIsNull() {
        // given
        List<CommandHandler<Command>> commandHandlers = Collections.emptyList();
        List<CommandValidator<Command, ErrorBuilder>> commandValidators = null;

        MediateConfigurer configurer = new MediateConfigurer();

        // when
        // then
        MediateJMissingArgumentException exception = assertThrowsExactly(MediateJMissingArgumentException.class, () -> configurer.register(commandHandlers, commandValidators));
        assertEquals("Command validators cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw MediateJMissingArgumentException if register command handler without generic parameters")
    public void shouldThrowExceptionIfRegisterCommandHandlerWithoutGenericParameters() {
        // given
        MediateConfigurer configurer = new MediateConfigurer();

        // when
        // then
        MediateJMissingArgumentException exception = assertThrowsExactly(MediateJMissingArgumentException.class,
                () -> configurer.register(new MediateTestClassPack.TestCommandHandlerWithoutParameters()));
        assertEquals("Class " + MediateTestClassPack.TestCommandHandlerWithoutParameters.class.getSimpleName() + " doesn't have required generic interface parameters", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw MediateJMissingArgumentException if register command validator without generic parameters")
    public void shouldThrowExceptionIfRegisterCommandValidatorWithoutGenericParameters() {
        // given
        MediateConfigurer configurer = new MediateConfigurer();

        // when
        // then
        MediateJMissingArgumentException exception = assertThrowsExactly(MediateJMissingArgumentException.class,
                () -> configurer.register(new MediateTestClassPack.TestCommandHandler(), new MediateTestClassPack.TestCommandValidatorWithoutParameters()));
        assertEquals("Class " + MediateTestClassPack.TestCommandValidatorWithoutParameters.class.getSimpleName() + " doesn't have required generic interface parameters", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw MediateJWrongParameterException if register command validator with additional generic parameters e.g. (T extends Command)")
    public void shouldThrowExceptionIfRegisterCommandValidatorWithAdditionalGenericParameterTypes() {
        // given
        MediateConfigurer configurer = new MediateConfigurer();
        configurer.registerErrorBuilder(new MediateTestClassPack.TestErrorBuilder());

        // when
        // then
        MediateJWrongParameterException exception = assertThrowsExactly(MediateJWrongParameterException.class,
                () -> configurer.register(new MediateTestClassPack.TestCommandHandler(), new MediateTestClassPack.TestCommandValidatorWithGenericParameters<>()));
        assertEquals("Wrong error builder parameter type for validator " + MediateTestClassPack.TestCommandValidatorWithGenericParameters.class.getSimpleName(), exception.getMessage());
    }

    @Test
    @DisplayName("Should throw MediateJWrongParameterException if register command validator with wrong command parameter type")
    public void shouldThrowExceptionIfRegisterCommandValidatorWithWrongCommandParameter() {
        // given
        MediateConfigurer configurer = new MediateConfigurer();
        configurer.registerErrorBuilder(new MediateTestClassPack.TestErrorBuilder());

        // when
        // then
        MediateJWrongParameterException exception = assertThrowsExactly(MediateJWrongParameterException.class,
                () -> configurer.register(new MediateTestClassPack.TestCommandHandler(), new MediateTestClassPack.TestCommandValidatorWithWrongCommandParameter<>()));
        assertEquals("Wrong first parameter type for validator " + MediateTestClassPack.TestCommandValidatorWithWrongCommandParameter.class.getSimpleName(), exception.getMessage());
    }

    @Test
    @DisplayName("Should register command handlers with proper command validators")
    public void shouldRegisterCommandHandlerListWithCommandValidators() {
        // given
        MediateConfigurer configurer = new MediateConfigurer();
        configurer.registerErrorBuilder(new MediateTestClassPack.TestErrorBuilder());

        Set<CommandBundle<? extends Command, ? extends ErrorBuilder>> commandBundles =
                Set.of(new CommandBundle<>(new MediateTestClassPack.TestCommandValidator(), new MediateTestClassPack.TestCommandHandler()),
                        new CommandBundle<>(new MediateTestClassPack.TestCommandValidatorSecond(), new MediateTestClassPack.SecondTestCommandHandler()));

        // when
        configurer.register(commandBundles);

        // then
        Map<String, CommandBundle<? extends Command, ? extends ErrorBuilder>> map = configurer.commandBundleMap();
        assertEquals(2, map.size());
        assertNotNull(map.get(MediateTestClassPack.TestCommand.class.getCanonicalName()));
        assertNotNull(map.get(MediateTestClassPack.SecondTestCommand.class.getCanonicalName()));
    }

}
