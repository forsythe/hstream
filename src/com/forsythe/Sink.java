package com.forsythe;


import java.util.Comparator;

public interface Sink {
    void accept(int i);

    /**
     * For some stream operations, it cannot continue unless it knows it has
     * processed all inputs, e.g. {@link com.forsythe.stage.HStream#sorted(Comparator)}.
     * <p>
     * In that case, the sort stage will wait until this function is called
     */
    void onComplete();
}
