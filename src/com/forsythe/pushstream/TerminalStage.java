package com.forsythe.pushstream;

public abstract class TerminalStage implements Sink {

    @Override
    public final void onReadyForNextStage() {
        //no-op
    }

    /**
     * A terminal stage that produces some output
     *
     * @param <OUTPUT> the type of the output being produced
     */
    abstract static class TerminalOperatorStage<OUTPUT> extends TerminalStage {
        abstract OUTPUT getResult();
    }

    /**
     * A terminal stage that consumes the output and produces nothing
     */
    abstract static class TerminalConsumerStage extends TerminalStage {
    }
}
