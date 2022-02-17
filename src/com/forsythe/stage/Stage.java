package com.forsythe.stage;

import com.forsythe.Sink;
import com.forsythe.stage.TerminalStage.TerminalConsumerStage;
import com.forsythe.stage.TerminalStage.TerminalOperatorStage;

import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;

/**
 * Represents a stage of the stream that takes some input and potentially produces some output
 */
public abstract class Stage implements HStream {
    protected Sink downstream;

    private Stage() {
        //no-overriding this class outside of here
    }


    @Override
    public HStream map(IntUnaryOperator mapper) {
        Stage op = new StatelessStage(this) {
            @Override
            public void accept(int value) {
                this.downstream.accept(mapper.applyAsInt(value));
            }
        };
        this.downstream = op;
        return op;
    }

    @Override
    public HStream flatMap(Function<Integer, Iterable<Integer>> mapper) {
        Stage op = new StatelessStage(this) {
            @Override
            public void accept(int value) {
                for (int i : mapper.apply(value)) {
                    this.downstream.accept(i);
                }
            }
        };
        this.downstream = op;
        return op;
    }

    @Override
    public HStream peek() {
        Stage op = new StatefulStage(this) {
            List<Integer> values = new ArrayList<>();

            @Override
            public void accept(int value) {
                values.add(value);
            }

            @Override
            public void onReadyForNextStage() {
                System.out.println(values.stream().map(String::valueOf).collect(Collectors.joining(" ")));
                for (int i : values) {
                    downstream.accept(i);
                }
                downstream.onReadyForNextStage();
            }
        };
        this.downstream = op;
        return op;
    }

    @Override
    public HStream filter(IntPredicate predicate) {
        Stage op = new StatelessStage(this) {

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
        Stage op = new StatefulStage(this) {
            PriorityQueue<Integer> heap = new PriorityQueue<>(comparator);

            @Override
            public void accept(int i) {
                heap.add(i);
            }

            @Override
            public void onReadyForNextStage() {
                while (!heap.isEmpty()) {
                    this.downstream.accept(heap.remove());
                }
                downstream.onReadyForNextStage();
            }
        };
        this.downstream = op;
        return op;
    }

    @Override
    public HStream limit(int limit) {
        Stage op = new StatelessStage(this) {
            int remaining = limit;

            @Override
            public void accept(int i) {
                if (remaining > 0) {
                    this.downstream.accept(i);
                    remaining--;
                }
            }
        };
        this.downstream = op;
        return op;
    }

    @Override
    public void forEach(Consumer<? super Integer> consumer) {
        this.downstream = new TerminalConsumerStage() {
            @Override
            public void accept(int i) {
                consumer.accept(i);
            }
        };
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
    public int reduce(int identity, ToIntBiFunction<Integer, Integer> combiner) {
        TerminalOperatorStage<Integer> tes = new TerminalOperatorStage<>() {
            int value = identity;

            @Override
            public Integer getResult() {
                return value;
            }

            @Override
            public void accept(int i) {
                value = combiner.applyAsInt(value, i);
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

    @Override
    public Optional<Integer> reduce(ToIntBiFunction<Integer, Integer> combiner) {
        TerminalOperatorStage<Optional<Integer>> tes = new TerminalOperatorStage<>() {
            boolean sawValue = false;
            int baseVal = 0;

            @Override
            Optional<Integer> getResult() {
                return sawValue ? Optional.of(baseVal) : Optional.empty();
            }

            @Override
            public void accept(int i) {
                if (!sawValue) {
                    baseVal = i;
                    sawValue = true;
                } else {
                    baseVal = combiner.applyAsInt(baseVal, i);
                }
            }
        };
        this.downstream = tes;
        evaluate();
        return tes.getResult();
    }

    @Override
    public Iterator<Integer> iterator() {
        List<Integer> output = toList();
        return output.iterator();
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
    private abstract static class StatelessStage extends Stage {
        private final Stage upstream;

        protected StatelessStage(Stage upstream) {
            this.upstream = upstream;
        }

        @Override
        public void evaluate() {
            this.upstream.evaluate();
        }

        @Override
        public final void onReadyForNextStage() {
            downstream.onReadyForNextStage();
        }
    }

    /**
     * An abstract class representing an intermediate stage that takes some input, and produces
     * some output, but maintains state. Therefore it awaits {@link #onReadyForNextStage()} before
     * finalizing its output and triggering downstream's {@link #onReadyForNextStage()}
     */
    private abstract static class StatefulStage extends Stage {
        private final Stage upstream;

        protected StatefulStage(Stage upstream) {
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
        @Override
        public final void accept(int i) {
            downstream.accept(i);
        }

        @Override
        public final void onReadyForNextStage() {
            //no-op, since nobody can tell us we're ready
        }

        @Override
        public final void evaluate() {
            this.loadData();
            downstream.onReadyForNextStage();
        }

        /**
         * This function should be overridden to load the input from list/array/whatever
         */
        protected abstract void loadData();
    }
}
