package com.forsythe.stage;

import com.forsythe.Sink;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntConsumer;
import java.util.function.IntPredicate;
import java.util.function.IntUnaryOperator;
import java.util.function.ToIntBiFunction;

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
        TerminalOperatorStage<Integer> tes = new TerminalOperatorStage<>() {
            int total = 0;

            @Override
            public Integer getResult() {
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

    @Override
    public int reduce(int identity, ToIntBiFunction<Integer, Integer> toIntBiFunction) {
        TerminalOperatorStage<Integer> tes = new TerminalOperatorStage<>() {
            int value = identity;

            @Override
            public Integer getResult() {
                return value;
            }

            @Override
            public void accept(int i) {
                value = toIntBiFunction.applyAsInt(value, i);
            }
        };
        this.downstream = tes;
        evaluate();
        return tes.getResult();
    }

    @Override
    public List<Integer> toList() {
        TerminalOperatorStage<List<Integer>> tes = new TerminalOperatorStage<>() {
            List<Integer> output = new ArrayList<>();

            @Override
            public List<Integer> getResult() {
                return output;
            }

            @Override
            public void accept(int i) {
                output.add(i);
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

    static abstract class HeadStage extends OperatorStage {
        public final void accept(int i) {
            downstream.accept(i);
        }
    }
}
