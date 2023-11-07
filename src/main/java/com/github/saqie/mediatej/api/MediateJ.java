package com.github.saqie.mediatej.api;

public interface MediateJ {

    <T extends Command, R extends ErrorBuilder> void send(T command);

    <T extends Request, R extends ErrorBuilder, E> E send(T request);


}
