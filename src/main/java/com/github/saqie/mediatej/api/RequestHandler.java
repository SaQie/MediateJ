package com.github.saqie.mediatej.api;

public interface RequestHandler<T extends Request<R>, R> {

    R handle(T request);
}
