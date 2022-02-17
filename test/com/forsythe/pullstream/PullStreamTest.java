package com.forsythe.pullstream;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
}