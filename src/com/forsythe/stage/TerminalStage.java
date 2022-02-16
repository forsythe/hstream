package com.forsythe.stage;

import com.forsythe.Sink;

public interface TerminalStage extends Sink {
    @Override
    default void onComplete() {
        //no-op
    }

    /**
     * A terminal stage that produces some output
     *
     * @param <OUTPUT> the type of the output being produced
     */
    interface TerminalOperatorStage<OUTPUT> extends TerminalStage {
        OUTPUT getResult();
    }

    /**
     * A terminal stage that consumes the output and produces nothing
     */
    interface TerminalConsumerStage extends TerminalStage {
    }
}
