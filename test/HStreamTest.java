import com.forsythe.stage.HStream;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HStreamTest {

    @Test
    void map() {
        HStream stream = HStream.fromList(List.of(1, 2, 3, 4));
        assertEquals(List.of(2, 4, 6, 8), stream.map(x -> 2 * x).toList());
    }

    @Test
    void filter() {
        HStream stream = HStream.fromRange(0, 6);
        assertEquals(List.of(0, 2, 4), stream.filter(x -> x % 2 == 0).toList());
    }

    @Test
    void sorted() {
        HStream stream = HStream.fromRange(1, 10);
        List<Integer> output = stream.map(x -> x * x).map(x -> x % 2 == 0 ? x : -x).sorted().map(x -> x * 10).toList();
        assertEquals(List.of(-810, -490, -250, -90, -10, 40, 160, 360, 640), output);
    }

    @Test
    void peek() {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        HStream stream = HStream.fromRange(1, 10);
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

    }

    @Test
    void forEach() {
        HStream stream = HStream.fromRange(-3, 3);
        Set<Integer> vals = new HashSet<>();
        stream.forEach(vals::add);
        assertEquals(Set.of(-3, -2, -1, 0, 1, 2), vals);
    }

    @Test
    void reduce() {
        HStream hstream = HStream.fromList(List.of(1, 2, 3, 4));
        int toPowersOf10 = hstream.reduce(0, (a, b) -> a * 10 + b);
        assertEquals(1234, toPowersOf10);
    }

    @Test
    void sum() {
        HStream stream = HStream.fromRange(1, 101);
        int sum = stream.sum();
        assertEquals((100 * 101) / 2, sum);
    }

    @Test
    void toList() {
        HStream stream = HStream.fromVarArgs(1, 2, 3, 4);
        assertEquals(List.of(1, 2, 3, 4), stream.toList());
    }

    @Test
    void fromList() {
        List<Integer> original = List.of(1, 2, 3, 4);
        HStream stream = HStream.fromList(original);
        assertEquals(original, stream.toList());
    }

    @Test
    void fromVarArgs() {
        HStream stream = HStream.fromVarArgs(-2, 0, 2, Integer.MAX_VALUE);
        assertEquals(List.of(-2, 0, 2, Integer.MAX_VALUE), stream.toList());
    }


    @Test
    void fromRange() {
        HStream stream = HStream.fromRange(1, 10);
        assertEquals(3 + 6 + 9, stream.filter(x -> x % 3 == 0).sum());
    }

    @Test
    void fromRangeMapFilterToList() {
        HStream stream = HStream.fromRange(1, 10);
        List<Integer> ans = stream.map(x -> x * x).filter(x -> x % 2 != 0).toList();
        assertEquals(List.of(1, 9, 25, 49, 81), ans);
    }
}