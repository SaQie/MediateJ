package com.github.saqie.mediatej.core;

import com.github.saqie.mediatej.core.configuration.ErrorBuilderInstanceMode;
import com.github.saqie.mediatej.core.configuration.HandlerConflictMode;
import com.github.saqie.mediatej.core.exception.MediateJMissingArgumentException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MediateCoreConfigurerTest {

    @Test
    @DisplayName("Should set mediate core configurer parameters correctly")
    public void shouldSetMediateCoreConfigurerParameters() {
        // given
        MediateCoreConfigurer coreConfigurer = new MediateCoreConfigurer();

        // when
        coreConfigurer
                .errorBuilderInstanceMode(ErrorBuilderInstanceMode.PER_SEND)
                .handlerConflictMode(HandlerConflictMode.THROW_EXCEPTION);

        // then
        assertEquals(HandlerConflictMode.THROW_EXCEPTION, coreConfigurer.handlerConflictMode());
        assertEquals(ErrorBuilderInstanceMode.PER_SEND, coreConfigurer.errorBuilderInstanceMode());
    }

    @Test
    @DisplayName("Should set default mediate core configurer parameters when parameters not given")
    public void shouldSetDefaultMediateCoreConfigurerParametersWhenCreate() {
        // given
        // when
        MediateCoreConfigurer coreConfigurer = new MediateCoreConfigurer();

        // then
        assertEquals(HandlerConflictMode.OVERRIDE, coreConfigurer.handlerConflictMode());
        assertEquals(ErrorBuilderInstanceMode.PER_SEND, coreConfigurer.errorBuilderInstanceMode());
    }

    @Test
    @DisplayName("Should throw MediateJMissingArgumentException if provided null in HandlerConflictMode parameter")
    public void shouldThrowExceptionIfProvidedMediateCoreConfigurerNullHandlerConflictModeParameter() {
        // given
        MediateCoreConfigurer coreConfigurer = new MediateCoreConfigurer();

        // when
        // then
        assertThrowsExactly(MediateJMissingArgumentException.class, () -> coreConfigurer
                .handlerConflictMode(null), "Handler conflict mode cannot be null");
    }

    @Test
    @DisplayName("Should throw MediateJMissingArgumentException if provided null in ErrorBuilderInstanceMode parameter")
    public void shouldThrowExceptionIfProvidedMediateCoreConfigurerNullErrorBuilderInstanceModeParameter() {
        // given
        MediateCoreConfigurer coreConfigurer = new MediateCoreConfigurer();

        // when
        // then
        assertThrowsExactly(MediateJMissingArgumentException.class, () -> coreConfigurer
                .errorBuilderInstanceMode(null), "Error builder instance mode cannot be null");
    }

}
