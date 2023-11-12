package com.github.saqie.mediatej.api;

import java.util.Optional;

public class RequestBundle<T extends Request<E>, R extends ErrorBuilder, E> {

    private final RequestHandler<T, E> requestHandler;
    private final RequestValidator<T, R> requestValidator;

    public RequestBundle(RequestHandler<T, E> requestHandler, RequestValidator<T, R> requestValidator) {
        this.requestHandler = requestHandler;
        this.requestValidator = requestValidator;
    }

    public RequestBundle(RequestHandler<T, E> requestHandler) {
        this.requestHandler = requestHandler;
        this.requestValidator = null;
    }

    public RequestHandler<T, E> requestHandler() {
        return requestHandler;
    }

    public Optional<RequestValidator<T, R>> requestValidator() {
        return Optional.ofNullable(requestValidator);
    }
}
