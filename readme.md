### Example

```java
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
```

Output
```
1 2 3 4 5 6 7 8 9
1 4 9 16 25 36 49 64 81
-1 4 -9 16 -25 36 -49 64 -81
-81 -49 -25 -9 -1 4 16 36 64
-49 -25 -9 -1 4 16 36 64
-490 -250 -90 -10 40 160 360 640

```