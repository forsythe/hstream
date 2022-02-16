package com.forsythe.stage;

public abstract class MidStage extends NonterminalStage {
    private NonterminalStage upstream;

    protected MidStage(NonterminalStage upstream) {
        this.upstream = upstream;
    }

    @Override
    public void evaluate(){
        this.upstream.evaluate();
    }
}
