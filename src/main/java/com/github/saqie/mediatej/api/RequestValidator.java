package com.github.saqie.mediatej.api;

public interface RequestValidator<T extends Request, R extends ErrorBuilder> {

    void validate(T request, R errorBuilder);

}
