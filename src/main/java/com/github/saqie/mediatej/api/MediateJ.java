package com.github.saqie.mediatej.api;

import com.github.saqie.mediatej.core.exception.MediateJMissingHandlerException;

public interface MediateJ {

    /**
     * Sends command to proper handler
     * Throws {@link MediateJMissingHandlerException} if the handler can't be found
     *
     * @param command -> Command instance to send
     */
    <T extends Command, R extends ErrorBuilder> void send(T command);

    /**
     * Sends request to proper handler
     * Throws {@link MediateJMissingHandlerException} if the handler can't be found
     *
     * @param request -> Request instance to send
     * @param <E>     -> Object to return
     */
    <T extends Request<E>, R extends ErrorBuilder, E> E send(T request);


}
