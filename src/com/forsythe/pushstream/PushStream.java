package com.forsythe.pushstream;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.IntPredicate;
import java.util.function.IntUnaryOperator;
import java.util.function.ToIntBiFunction;

/**
 * The interface the user expects when given a stream
 */
public interface PushStream extends Sink, Iterable<Integer> {


    /**
     * Static factory methods
     **/
    static PushStream fromList(List<Integer> list) {
        return new Stage.HeadStage() {
            @Override
            protected void loadData() {
                for (int i : list) {
                    accept(i);
                }
            }
        };
    }

    static PushStream of(int... nums) {
        return new Stage.HeadStage() {
            @Override
            protected void loadData() {
                for (int i : nums) {
                    accept(i);
                }
            }
        };
    }

    static PushStream concat(PushStream... streams) {
        return new Stage.HeadStage() {
            @Override
            protected void loadData() {
                for (PushStream stream : streams) {
                    for (int i : stream) {
                        accept(i);
                    }
                }
            }
        };
    }

    static PushStream fromRange(int fromIncl, int toExcl) {
        return new Stage.HeadStage() {
            @Override
            protected void loadData() {
                for (int i = fromIncl; i < toExcl; i++) {
                    accept(i);
                }
            }
        };
    }

    /**
     * Nonterminal operations
     **/
    PushStream map(IntUnaryOperator mapper);

    PushStream flatMap(Function<Integer, Iterable<Integer>> mapper);

    PushStream peek();

    PushStream filter(IntPredicate predicate);

    PushStream sorted(Comparator<Integer> comparator);

    PushStream limit(int limit);

    PushStream skip(int skip);

    default PushStream sorted() {
        return sorted(Integer::compare);
    }

    /**
     * Terminal operations
     **/

    int fold(int identity, ToIntBiFunction<Integer, Integer> combiner);

    Optional<Integer> fold(ToIntBiFunction<Integer, Integer> combiner);

    default Optional<Integer> max() {
        return fold(Math::max);
    }

    default Optional<Integer> min() {
        return fold(Math::min);
    }

    int count();

    int sum();

    List<Integer> toList();
}
