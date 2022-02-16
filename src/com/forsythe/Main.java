package com.forsythe;

import com.forsythe.stage.HeadStage;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        HStream hstream = HStream.fromList(List.of(1, 2, 3, 4));
        hstream.map(x -> x * x).filter(x -> x % 2 == 0).forEach(System.out::println);

    }
}
