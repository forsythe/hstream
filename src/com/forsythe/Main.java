package com.forsythe;

import com.forsythe.stage.HStream;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        HStream stream = HStream.fromRange(1, 10);
        List<Integer> output = stream
                .peek()
                .map(x -> x * x)
                .peek()
                .map(x -> x % 2 == 0 ? x : -x)
                .peek()
                .sorted()
                .peek()
                .filter(x->Math.abs(x) <= 70)
                .peek()
                .map(x -> x * 10)
                .peek()
                .toList();
    }
}
