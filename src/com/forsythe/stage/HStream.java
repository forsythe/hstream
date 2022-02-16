package com.forsythe.stage;

import com.forsythe.Sink;

import java.util.Comparator;
import java.util.List;
import java.util.function.IntConsumer;
import java.util.function.IntPredicate;
import java.util.function.IntUnaryOperator;
import java.util.function.ToIntBiFunction;

/**
 * The interface the user expects when given a stream
 */
public interface HStream extends Sink {
    /**
     * Nonterminal operations
     **/
    HStream map(IntUnaryOperator mapper);
    HStream peek();

    HStream filter(IntPredicate predicate);

    HStream sorted(Comparator<Integer> comparator);

    default HStream sorted() {
        return sorted(Integer::compare);
    }

    /**
     * Terminal operations
     **/
    void forEach(IntConsumer consumer);

    int reduce(int identity, ToIntBiFunction<Integer, Integer> toIntBiFunction);

    int sum();

    List<Integer> toList();

    /**
     * Static factory methods
     **/
    static HStream fromList(List<Integer> list) {
        return new Stage.HeadStage() {

            @Override
            protected void loadData() {
                for (int i : list) {
                    accept(i);
                }
            }
        };
    }

    static HStream fromVarArgs(int... nums) {
        return new Stage.HeadStage() {
            @Override
            protected void loadData() {
                for (int i : nums) {
                    accept(i);
                }
            }
        };
    }

    static HStream fromRange(int fromIncl, int toExcl) {
        return new Stage.HeadStage() {
            @Override
            protected void loadData() {
                for (int i = fromIncl; i < toExcl; i++) {
                    accept(i);
                }
            }
        };
    }
}
