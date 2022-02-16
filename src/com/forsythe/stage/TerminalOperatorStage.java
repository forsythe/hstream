package com.forsythe.stage;

import com.forsythe.Sink;

public interface TerminalOperatorStage<OUTPUT> extends Sink {
    OUTPUT getResult();
}
