package com.forsythe.pushstream;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class PushStreamTest {

    @Test
    void map() {
        PushStream stream = PushStream.fromList(List.of(1, 2, 3, 4));
        assertEquals(List.of(2, 4, 6, 8), stream.map(x -> 2 * x).toList());
    }

    @Test
    void flatMapAndIterator() {
        PushStream stream = PushStream.fromList(List.of(1, 2, 3, 4));
        assertEquals(List.of(1, 1, 2, 1, 2, 3, 1, 2, 3, 4), stream.flatMap(x -> PushStream.fromRange(1, x + 1)).toList());
    }

    @Test
    void filter() {
        PushStream stream = PushStream.fromRange(0, 6);
        assertEquals(List.of(0, 2, 4), stream.filter(x -> x % 2 == 0).toList());
        assertEquals(List.of(), stream.filter(x -> x >= 6).toList());
    }

    @Test
    void sorted() {
        PushStream stream = PushStream.fromRange(1, 10);
        List<Integer> output = stream.map(x -> x * x).map(x -> x % 2 == 0 ? x : -x).sorted().map(x -> x * 10).toList();
        assertEquals(List.of(-810, -490, -250, -90, -10, 40, 160, 360, 640), output);
    }

    @Test
    void limitAndSkip() {
        PushStream firstHalf = PushStream.fromRange(1, 10).limit(5);
        PushStream secondHalf = PushStream.fromRange(1, 10).skip(5); //TODO: fix, since limit/skip are use-once, make streams use-once as well
        assertEquals(PushStream.fromRange(1, 10).toList(), PushStream.concat(firstHalf, secondHalf).toList());
    }

    @Test
    void peek() {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        PushStream stream = PushStream.fromRange(1, 10);
        List<Integer> output = stream
                .peek()
                .map(x -> x * x)
                .peek()
                .map(x -> x % 2 == 0 ? x : -x)
                .peek()
                .sorted()
                .peek()
                .filter(x -> Math.abs(x) <= 70)
                .peek()
                .map(x -> x * 10)
                .peek()
                .toList();
        String expected = "1 2 3 4 5 6 7 8 9\r\n" +
                "1 4 9 16 25 36 49 64 81\r\n" +
                "-1 4 -9 16 -25 36 -49 64 -81\r\n" +
                "-81 -49 -25 -9 -1 4 16 36 64\r\n" +
                "-49 -25 -9 -1 4 16 36 64\r\n" +
                "-490 -250 -90 -10 40 160 360 640\r\n";
        assertEquals(expected, outContent.toString());
        System.setOut(System.out);
        assertEquals(List.of(-490, -250, -90, -10, 40, 160, 360, 640), output);
    }

    @Test
    void forEach() {
        PushStream stream = PushStream.fromRange(-3, 3);
        Set<Integer> vals = new HashSet<>();
        stream.forEach(vals::add);
        assertEquals(Set.of(-3, -2, -1, 0, 1, 2), vals);
    }

    @Test
    void reduce() {
        PushStream hstream = PushStream.fromList(List.of(1, 2, 3, 4));
        int toPowersOf10 = hstream.reduce(0, (a, b) -> a * 10 + b);
        assertEquals(1234, toPowersOf10);

        PushStream reduceWithoutIdentity = PushStream.fromRange(1, 10);
        assertEquals(123456789, reduceWithoutIdentity.reduce((a, b) -> a * 10 + b).orElse(-1));
    }

    @Test
    void sum() {
        PushStream stream = PushStream.fromRange(1, 101);
        int sum = stream.sum();
        assertEquals((100 * 101) / 2, sum);
    }

    @Test
    void count() {
        PushStream squares = PushStream.fromRange(0, 101);
        int powerOf2 = squares.filter(x -> Integer.bitCount(x) <= 1).peek().count();
        assertEquals(8, powerOf2);
    }

    @Test
    void toList() {
        PushStream stream = PushStream.of(1, 2, 3, 4);
        assertEquals(List.of(1, 2, 3, 4), stream.toList());
    }

    @Test
    void maxMin() {
        PushStream pushStream = PushStream.of(-10, Integer.MAX_VALUE);
        assertEquals(Integer.MAX_VALUE, pushStream.max().orElse(-1));
        assertEquals(-10, pushStream.min().orElse(1));
        PushStream empty = PushStream.of();
        assertFalse(empty.max().isPresent());
        assertFalse(empty.min().isPresent());
        PushStream twoNegatives = PushStream.of(-10, -5);
        assertEquals(-5, twoNegatives.max().orElse(-1));
        assertEquals(-10, twoNegatives.min().orElse(-1));
    }


    @Test
    void fromList() {
        List<Integer> original = List.of(1, 2, 3, 4);
        PushStream stream = PushStream.fromList(original);
        assertEquals(original, stream.toList());
    }

    @Test
    void of() {
        PushStream stream = PushStream.of(-2, 0, 2, Integer.MAX_VALUE);
        assertEquals(List.of(-2, 0, 2, Integer.MAX_VALUE), stream.toList());
    }

    @Test
    void concat() {
        PushStream evens = PushStream.fromRange(0, 10).filter(x -> x % 2 == 0);
        PushStream odds = PushStream.fromRange(0, 10).filter(x -> x % 2 != 0);
        PushStream combined = PushStream.concat(evens, odds);
        assertEquals(List.of(0, 2, 4, 6, 8, 1, 3, 5, 7, 9), combined.toList());
        assertEquals(PushStream.fromRange(0, 10).toList(), combined.sorted().toList());
    }

    @Test
    void fromRange() {
        PushStream stream = PushStream.fromRange(1, 10);
        assertEquals(3 + 6 + 9, stream.filter(x -> x % 3 == 0).sum());
    }

    @Test
    void fromRangeMapFilterToList() {
        PushStream stream = PushStream.fromRange(1, 10);
        List<Integer> ans = stream.map(x -> x * x).filter(x -> x % 2 != 0).toList();
        assertEquals(List.of(1, 9, 25, 49, 81), ans);
    }

    @Test
    void emptyList() {
        PushStream stream = PushStream.of();
        assertTrue(stream.map(x -> x * x).filter(x -> x % 2 != 0).toList().isEmpty());
    }

    @Test
    void quicksort() {
        PushStream stream = PushStream.of(3, 2, 1, 5, 4, 9, 7, 6, 8, 0);
        PushStream sorted = qs(stream);
        assertEquals(stream.sorted().toList(), sorted.toList());
    }

    private PushStream qs(PushStream stream) {
        if (stream.count() <= 1)
            return stream;

        List<Integer> elements = stream.toList();
        int pivot = elements.get(0);
        List<Integer> rest = elements.subList(1, elements.size());
        PushStream less = qs(PushStream.fromList(rest).filter(x -> x <= pivot));
        PushStream greater = qs(PushStream.fromList(rest).filter(x -> x > pivot));
        return PushStream.concat(less, PushStream.of(pivot), greater);
    }
}