package com.forsythe.pullstream;

public abstract class HeadStage extends Stage {

    public HeadStage(Source upstream) {
        super(upstream);
    }

    @Override
    public int getNext() {
        return upstream.getNext();
    }
}
