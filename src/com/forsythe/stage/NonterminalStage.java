package com.forsythe.stage;

import com.forsythe.HStream;
import com.forsythe.Sink;

import java.util.function.IntConsumer;
import java.util.function.IntPredicate;
import java.util.function.IntUnaryOperator;

public abstract class NonterminalStage implements HStream {
    protected Sink downstream;

    @Override
    public HStream map(IntUnaryOperator mapper) {
        NonterminalStage nonterminalStage = new MidStage(this) {
            /**
             * Called by my upstream. I should accept the value and pass to downstream sink
             */
            @Override
            public void accept(int value) {
                this.downstream.accept(mapper.applyAsInt(value));
            }
        };
        this.downstream = nonterminalStage;
        return nonterminalStage;
    }

    @Override
    public HStream filter(IntPredicate predicate) {
        NonterminalStage nonterminalStage = new MidStage(this) {

            /**
             * Called by my upstream. I should accept the value, and filter it to my downstream
             */
            @Override
            public void accept(int i) {
                if (predicate.test(i)) {
                    this.downstream.accept(i);
                }
            }
        };
        this.downstream = nonterminalStage;
        return nonterminalStage;
    }

    @Override
    public void forEach(IntConsumer consumer) {
        this.downstream = (TerminalStage) consumer::accept;
        evaluate();
    }

    public abstract void evaluate();
}
