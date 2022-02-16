package com.forsythe.stage;

import com.forsythe.Sink;

import java.util.function.IntConsumer;
import java.util.function.IntPredicate;
import java.util.function.IntUnaryOperator;

public abstract class OperatorStage implements HStream {
    protected Sink downstream;

    @Override
    public HStream map(IntUnaryOperator mapper) {
        OperatorStage nonterminalStage = new StatelessOperator(this) {
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
        OperatorStage nonterminalStage = new StatelessOperator(this) {

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
        this.downstream = (TerminalConsumerStage) consumer::accept;
        evaluate();
    }

    @Override
    public int sum() {
        TerminalOperatorStage tes = new TerminalOperatorStage() {
            int total = 0;

            @Override
            public int getResult() {
                return total;
            }

            @Override
            public void accept(int i) {
                total += i;
            }
        };
        this.downstream = tes;

        evaluate();
        return tes.getResult();
    }

    protected abstract void evaluate();

    private abstract static class StatelessOperator extends OperatorStage {
        private final OperatorStage upstream;

        protected StatelessOperator(OperatorStage upstream) {
            this.upstream = upstream;
        }

        @Override
        public void evaluate() {
            this.upstream.evaluate();
        }
    }

    static final class HeadStage extends OperatorStage {
        private final Iterable<Integer> iterable;

        public HeadStage(Iterable<Integer> iterable) {
            this.iterable = iterable;
        }

        @Override
        public void evaluate() {
            for (int i : iterable) {
                this.accept(i);
            }
        }

        @Override
        public void accept(int i) {
            downstream.accept(i);
        }
    }
}
