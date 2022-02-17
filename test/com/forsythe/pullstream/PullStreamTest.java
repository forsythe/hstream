package com.forsythe.pullstream;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PullStreamTest {
    @Test
    void map() {
        PullStream stream = PullStream.fromList(List.of(1, 2, 3));
        List<Integer> output = stream.map(x -> x * x).toList();
        assertEquals(List.of(1, 4, 9), output);
        //assert stream is consumed
        assertEquals(List.of(), stream.toList());
    }

    @Test
    void filter() {
        //filter all
        PullStream stream = PullStream.fromList(List.of(1, 2, 3));
        assertEquals(List.of(), stream.filter(x -> x > 4).toList());
        assertEquals(List.of(), stream.toList()); //fully consumed
        //filter partial
        PullStream stream2 = PullStream.fromList(List.of(1, 2, 3));
        assertEquals(List.of(2), stream2.filter(x -> x % 2 == 0).toList());
        assertEquals(List.of(), stream2.toList()); //fully consumed
        //filter none
        PullStream stream3 = PullStream.fromList(List.of(1, 2, 3));
        assertEquals(List.of(1, 2, 3), stream3.filter(x -> x < 4).toList());
        assertEquals(List.of(), stream3.toList()); //fully consumed
    }

    @Test
    void mapAndFilter() {
        PullStream stream = PullStream.fromRange(1, 100);
        List<Integer> output = stream.filter(i -> Integer.bitCount(i) == 1).map(x -> -x).toList();
        assertEquals(List.of(-1, -2, -4, -8, -16, -32, -64), output);
    }

    @Test
    void sorted() {
        PullStream stream = PullStream.fromRange(1, 10).map(x -> x % 2 == 0 ? -x : x);
        List<Integer> output = stream.sorted().toList();
        assertEquals(List.of(-8, -6, -4, -2, 1, 3, 5, 7, 9), output);
    }

    @Test
    void generatorAndLimit() {
        PullStream counter = PullStream.generator(1, (a) -> a + 1);
        List<Integer> output = counter.map(x -> x * x).limit(5).toList();
        assertEquals(List.of(1, 4, 9, 16, 25), output);

        PullStream counter2 = PullStream.generator(1, (a) -> a + 1);
        List<Integer> output2 = counter2.map(x -> x * x).limit(0).toList();
        assertTrue(output2.isEmpty());
    }

    @Test
    void skip() {
        //skip on infinite
        PullStream counter = PullStream.generator(1, x -> x + 1);
        List<Integer> output = counter.skip(100).limit(5).skip(1).toList();
        assertEquals(List.of(102, 103, 104, 105), output);
        //skip entire list
        assertEquals(List.of(), PullStream.fromList(List.of(1, 2, 3)).skip(3).toList());
        //skip nothing
        assertEquals(List.of(1, 2, 3), PullStream.fromList(List.of(1, 2, 3)).skip(0).toList());
    }

    @Test
    void iteration() {
        PullStream counter = PullStream.generator(1, x -> 2 * x);
        assertEquals(List.of(1, 2, 4, 8), counter.limit(4).toList());
        Set<Integer> output = new HashSet<>();
        //using the same infinite counter
        counter.limit(4).forEach(output::add);
        assertEquals(Set.of(16, 32, 64, 128), output);

        output.clear();
        for (int i : counter) {
            output.add(i);
            if (output.size() == 4)
                break;
        }
        assertEquals(Set.of(256, 512, 1024, 2048), output);
    }

    @Test
    void fold() {
        PullStream counter = PullStream.generator(1, x -> x + 1);
        int ans = counter.limit(5).fold(0, (a, b) -> a * 10 + b);
        assertEquals(12345, ans);
    }

    @Test
    void foldObj() {
        PullStream counter = PullStream.generator(1, x -> x + 1);
        Map<Integer, Integer> count = new HashMap<>();
        counter.limit(5).fold(count, (a, b) -> a.merge(b, 1, Integer::sum));
        assertEquals(Map.of(1, 1,
                2, 1,
                3, 1,
                4, 1,
                5, 1), count);
    }

    @Test
    void flatMap() {
        PullStream counter = PullStream.generator(1, x -> x + 1)
                .limit(3)
                .flatMap(x -> PullStream.generator(x, a -> a).limit(x));
        assertEquals(List.of(1, 2, 2, 3, 3, 3), counter.toList());
    }
}