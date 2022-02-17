package com.forsythe.pullstream;

import javax.crypto.spec.PSource;

public class HeadStage extends Stage {

    public HeadStage(Source upstream) {
        super(upstream);
    }

    @Override
    public boolean hasNext() {
        return upstream.hasNext();
    }

    @Override
    public int getNext() {
        return upstream.getNext();
    }
}
