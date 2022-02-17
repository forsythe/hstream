package com.forsythe;

import com.forsythe.pushstream.PushStream;

public class Main {
    public static void main(String[] args) {
        PushStream stream = PushStream.fromRange(0, 10);
        stream.flatMap(x -> PushStream.of(-x, x)).limit(5).peek().toList();
    }
}
