package com.forsythe;

import java.util.function.IntConsumer;

@FunctionalInterface
public interface Sink {
    void accept(int i);
}
