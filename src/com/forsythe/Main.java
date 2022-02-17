package com.forsythe;

import com.forsythe.stage.HStream;

public class Main {
    public static void main(String[] args) {
        HStream stream = HStream.fromRange(0, 10);
        stream.flatMap(x -> HStream.of(-x, x)).limit(5).peek().toList();
    }
}
