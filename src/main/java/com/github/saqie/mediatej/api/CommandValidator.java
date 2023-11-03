package com.github.saqie.mediatej.api;

public interface CommandValidator<T extends Command, R extends ErrorBuilder> {

    void validate(T command, R errorBuilder);

}
