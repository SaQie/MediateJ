package com.github.saqie.mediatej.api;

public interface CommandHandler<T extends Command> {

    void handle(T command);


}
