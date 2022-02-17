package com.forsythe.pullstream;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.function.IntBinaryOperator;
import java.util.function.IntPredicate;
import java.util.function.IntUnaryOperator;

/**
 * Interface representing a single use lazy stream of integers
 */
public interface PullStream extends Source, Iterable<Integer> {
    PullStream map(IntUnaryOperator mapper);


    PullStream filter(IntPredicate mapper);

    PullStream sorted(Comparator<Integer> comparator);

    default PullStream sorted() {
        return sorted(Integer::compare);
    }

    PullStream limit(int limit);

    PullStream skip(int skip);
    int reduce(int identity, IntBinaryOperator reducer);

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
        });
    }

    static PullStream fromRange(int startIncl, int endExcl) {
        return new HeadStage(new Source() {
            int cur = startIncl;

            @Override
            public boolean hasNext() {
                return cur < endExcl;
            }

            @Override
            public int getNext() {
                return cur++;
            }
        });
    }

    static PullStream generator(int base, IntUnaryOperator generator) {
        return new HeadStage(new Source() {
            int val = base;

            @Override
            public boolean hasNext() {
                return true;
            }

            @Override
            public int getNext() {
                int retVal = val;
                val = generator.applyAsInt(val);
                return retVal;
            }
        });
    }
}
