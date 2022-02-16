package com.forsythe;

import com.forsythe.stage.HStream;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        HStream hstream = HStream.fromList(List.of(1, 2, 3, 4));

        int sum = hstream.map(x -> x * x).filter(x -> x % 2 == 0).sum();
        System.out.println(sum); //2^2 + 4^2 = 4+16=20

        hstream.forEach(System.out::println); //prints the list

        int toPowersOf10 = hstream.reduce(0, (a, b) -> a * 10 + b);
        System.out.println(toPowersOf10); //1234
    }
}
