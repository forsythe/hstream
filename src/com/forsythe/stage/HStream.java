package com.forsythe.stage;

import com.forsythe.Sink;

import java.util.List;
import java.util.function.IntConsumer;
import java.util.function.IntPredicate;
import java.util.function.IntUnaryOperator;

public interface HStream extends Sink {
    /**Nonterminal operations**/
    HStream map(IntUnaryOperator mapper);

    HStream filter(IntPredicate predicate);

    /**Terminal operations**/
    void forEach(IntConsumer consumer);
    int sum();

    /**Static factory methods**/
    static HStream fromList(List<Integer> list) {
        return new OperatorStage.HeadStage(list);
    }
}
