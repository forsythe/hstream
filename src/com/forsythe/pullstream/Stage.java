package com.forsythe.pullstream;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.function.IntPredicate;
import java.util.function.IntUnaryOperator;

public abstract class Stage implements PullStream {
    protected Source upstream;

    protected Stage(Source upstream) {
        this.upstream = upstream;
    }

    @Override
    public PullStream map(IntUnaryOperator mapper) {
        return new Stage(this) {
            @Override
            public int getNext() {
                return mapper.applyAsInt(upstream.getNext());
            }

            @Override
            public boolean hasNext() {
                return upstream.hasNext();
            }
        };
    }

    @Override
    public PullStream filter(IntPredicate pred) {
        return new Stage(this) {
            boolean holdingValidValue = false;
            int value = -1;

            @Override
            public int getNext() {
                if (!holdingValidValue)
                    throw new RuntimeException("filter has no valid values");
                holdingValidValue = false; //consume the value
                return value;
            }

            /**
             * @return Whether there is at least 1 remaining element left that would pass the filter
             */
            @Override
            public boolean hasNext() {
                if (holdingValidValue)
                    return true;
                if (!upstream.hasNext())
                    return false;

                while (upstream.hasNext()) {
                    int upstreamVal = upstream.getNext();
                    if (pred.test(upstreamVal)) {
                        holdingValidValue = true;
                        value = upstreamVal;
                        return true;
                    }
                }
                return false;
            }
        };
    }

    @Override
    public PullStream sorted(Comparator<Integer> comparator) {
        return new Stage(this) {
            PriorityQueue<Integer> pq = new PriorityQueue<>(comparator);

            @Override
            public int getNext() {
                return pq.remove();
            }

            @Override
            public boolean hasNext() {
                while (upstream.hasNext()) {
                    pq.add(upstream.getNext());
                }
                return !pq.isEmpty();
            }
        };
    }

    @Override
    public PullStream limit(int limit) {
        return new Stage(this) {
            int remaining = limit;

            @Override
            public int getNext() {
                remaining--;
                return upstream.getNext();
            }


            @Override
            public boolean hasNext() {
                return remaining > 0 && upstream.hasNext();
            }
        };
    }

    @Override
    public List<Integer> toList() {
        List<Integer> ans = new ArrayList<>();
        while (hasNext()) {
            ans.add(getNext());
        }
        return ans;
    }

}
