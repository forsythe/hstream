### Example

```java
//Eagerly evaluated
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
```

Output
```
[0, 0, -1, 1, -4, 4, -9, 9, -16, 16]
1
1
2
3
5
8
13
21
34
55
89
```