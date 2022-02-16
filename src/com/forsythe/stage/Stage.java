package com.forsythe.stage;

import com.forsythe.Sink;
import com.forsythe.stage.TerminalStage.TerminalConsumerStage;
import com.forsythe.stage.TerminalStage.TerminalOperatorStage;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.function.IntConsumer;
import java.util.function.IntPredicate;
import java.util.function.IntUnaryOperator;
import java.util.function.ToIntBiFunction;

/**
 * Represents a stage of the stream that takes some input and potentially produces some output
 */
public abstract class Stage implements HStream {
    protected Sink downstream;

    @Override
    public void onComplete() {
        downstream.onComplete();
    }

    @Override
    public HStream map(IntUnaryOperator mapper) {
        Stage op = new OperatorStage(this) {
            /**
             * Called by my upstream. I should accept the value and pass to downstream sink
             */
            @Override
            public void accept(int value) {
                this.downstream.accept(mapper.applyAsInt(value));
            }
        };
        this.downstream = op;
        return op;
    }

    @Override
    public HStream filter(IntPredicate predicate) {
        Stage op = new OperatorStage(this) {

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
        this.downstream = op;
        return op;
    }

    @Override
    public HStream sorted(Comparator<Integer> comparator) {
        Stage op = new OperatorStage(this) {
            PriorityQueue<Integer> heap = new PriorityQueue<>(comparator);

            @Override
            public void accept(int i) {
                heap.add(i);
            }

            @Override
            public void onComplete() {
                while (!heap.isEmpty()) {
                    this.downstream.accept(heap.remove());
                }
                downstream.onComplete();
            }
        };
        this.downstream = op;
        return op;
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

    /**
     * Used to trigger the upstream stage's evaluate. Eventually calls the {@link HeadStage}'s
     * {@link HeadStage#accept(int)}, which triggers the whole pipeline of execution
     */
    protected abstract void evaluate();


    /**
     * An abstract class representing an intermediate stage that takes some input, and produces
     * some output deterministically. should be pure function
     */
    private abstract static class OperatorStage extends Stage {
        private final Stage upstream;

        protected OperatorStage(Stage upstream) {
            this.upstream = upstream;
        }

        @Override
        public void evaluate() {
            this.upstream.evaluate();
        }
    }


    /**
     * An abstract class that should be overridden for initializing a stream. The {@link Stage#evaluate()} method
     * should be overridden in such a way as to consume the input correctly from the constructor of HeadStage's derived
     * classes
     */
    static abstract class HeadStage extends Stage {
        public final void accept(int i) {
            downstream.accept(i);
        }
    }
}