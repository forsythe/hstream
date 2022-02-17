package com.forsythe.pullstream;

import java.util.Iterator;
import java.util.List;
import java.util.function.IntUnaryOperator;

public interface PullStream extends Source {
    PullStream map(IntUnaryOperator mapper);

    List<Integer> toList();

    static PullStream fromList(List<Integer> input) {
        return new HeadStage(new Source() {
            Iterator<Integer> inputIter = input.iterator();

            @Override
            public boolean hasNext() {
                return inputIter.hasNext();
            }

            @Override
            public int getNext() {
                return inputIter.next();
            }
        }) {
        };
    }
}
