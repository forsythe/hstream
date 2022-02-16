package com.forsythe;

import com.forsythe.stage.HeadStage;

import java.util.List;
import java.util.function.IntConsumer;
import java.util.function.IntPredicate;
import java.util.function.IntUnaryOperator;

public interface HStream extends Sink {
    HStream map(IntUnaryOperator mapper);

    HStream filter(IntPredicate predicate);

    void forEach(IntConsumer consumer);

    static HStream fromList(List<Integer> list) {
        return new HeadStage(list);
    }
}
