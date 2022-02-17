package com.forsythe.pullstream;

import java.util.ArrayList;
import java.util.List;
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

    @Override
    public boolean hasNext() {
        return upstream.hasNext();
    }
}
