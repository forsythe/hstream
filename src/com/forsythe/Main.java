package com.forsythe;

import com.forsythe.pullstream.PullStream;

import java.util.List;

public class Main {
    public static void main(String[] args) {
//        PushStream stream = PushStream.fromRange(0, 10);
//        stream.flatMap(x -> PushStream.of(-x, x)).limit(5).peek().toList();
        PullStream stream = PullStream.fromList(List.of(1, 2, 3));
        List<Integer> output = stream.map(x -> x * x).toList();
        System.out.println(output);
    }
}
