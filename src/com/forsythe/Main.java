package com.forsythe;

import com.forsythe.pullstream.PullStream;
import com.forsythe.pushstream.PushStream;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        PushStream pushStream = PushStream.fromRange(0, 10);
        List<Integer> pushOutput = pushStream.flatMap(x -> PushStream.of(-x, x)).limit(5).toList();
        System.out.println(pushOutput);

        PullStream pullStream = PullStream.fromList(List.of(1, 2, 3));
        List<Integer> pullOutput = pullStream.map(x -> x * x).toList();
        System.out.println(pullOutput);
    }
}
