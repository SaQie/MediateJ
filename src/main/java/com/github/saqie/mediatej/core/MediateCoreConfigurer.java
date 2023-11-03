package com.github.saqie.mediatej.core;

import com.github.saqie.mediatej.core.configuration.ErrorBuilderInstanceMode;
import com.github.saqie.mediatej.core.configuration.HandlerConflictMode;

import static com.github.saqie.mediatej.core.Check.*;

public final class MediateCoreConfigurer {

    private HandlerConflictMode handlerConflictMode;
    private ErrorBuilderInstanceMode errorBuilderInstanceMode;

    public MediateCoreConfigurer handlerConflictMode(HandlerConflictMode handlerConflictMode) {
        requireNonNullArgument(handlerConflictMode, "Handler conflict mode cannot be null");
        this.handlerConflictMode = handlerConflictMode;
        return this;
    }

    public MediateCoreConfigurer errorBuilderInstanceMode(ErrorBuilderInstanceMode errorBuilderInstanceMode) {
        requireNonNullArgument(errorBuilderInstanceMode, "Error builder instance mode cannot be null");
        this.errorBuilderInstanceMode = errorBuilderInstanceMode;
        return this;
    }

    public MediateConfigurer build() {
        return new MediateConfigurer(this);
    }

    HandlerConflictMode handlerConflictMode() {
        return handlerConflictMode == null ? HandlerConflictMode.OVERRIDE : handlerConflictMode;
    }

    ErrorBuilderInstanceMode errorBuilderInstanceMode() {
        return errorBuilderInstanceMode == null ? ErrorBuilderInstanceMode.ONE : errorBuilderInstanceMode;
    }


}
