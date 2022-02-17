package com.forsythe.stage;

import com.forsythe.Sink;

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
public interface HStream extends Sink, Iterable<Integer> {


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

    static HStream of(int... nums) {
        return new Stage.HeadStage() {
            @Override
            protected void loadData() {
                for (int i : nums) {
                    accept(i);
                }
            }
        };
    }

    static HStream concat(HStream... streams) {
        return new Stage.HeadStage() {
            @Override
            protected void loadData() {
                for (HStream stream : streams) {
                    for (int i : stream) {
                        accept(i);
                    }
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

    /**
     * Nonterminal operations
     **/
    HStream map(IntUnaryOperator mapper);

    HStream flatMap(Function<Integer, Iterable<Integer>> mapper);

    HStream peek();

    HStream filter(IntPredicate predicate);

    HStream sorted(Comparator<Integer> comparator);

    HStream limit(int limit);

    HStream skip(int skip);

    default HStream sorted() {
        return sorted(Integer::compare);
    }

    /**
     * Terminal operations
     **/

    int reduce(int identity, ToIntBiFunction<Integer, Integer> combiner);

    Optional<Integer> reduce(ToIntBiFunction<Integer, Integer> combiner);

    default Optional<Integer> max() {
        return reduce(Math::max);
    }

    default Optional<Integer> min() {
        return reduce(Math::min);
    }

    int count();

    int sum();

    List<Integer> toList();
}
