package com.forsythe;

import com.forsythe.pullstream.PullStream;
import com.forsythe.pushstream.PushStream;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        PushStream pushStream = PushStream.fromRange(0, 10);
        List<Integer> pushOutput = pushStream.flatMap(x -> PushStream.of(-x * x, x * x)).limit(10).toList();
        System.out.println(pushOutput);

        //Lazily evaluated
        int[] prev = new int[]{0};
        PullStream.generator(1, (cur) -> {
            int temp = prev[0];
            prev[0] = cur;
            return cur + temp;
        }).takeWhile(x -> x < 100)
                .forEach(System.out::println);
    }
}
