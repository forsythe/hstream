package com.forsythe.pullstream;

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
