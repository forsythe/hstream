package com.forsythe.stage;

public class HeadStage extends NonterminalStage {
    Iterable<Integer> iterable;

    public HeadStage(Iterable<Integer> iterable) {
        this.iterable = iterable;
    }

    @Override
    public void evaluate() {
        for (int i : iterable) {
            this.accept(i);
        }
    }

    @Override
    public void accept(int i) {
        downstream.accept(i);
    }
}
