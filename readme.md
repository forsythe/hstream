### Example

```java
PushStream pushStream = PushStream.fromRange(0, 10);
List<Integer> pushOutput = pushStream.flatMap(x -> PushStream.of(-x, x)).limit(5).toList();
System.out.println(pushOutput);

PullStream pullStream = PullStream.fromList(List.of(1, 2, 3));
List<Integer> pullOutput = pullStream.map(x -> x * x).toList();
System.out.println(pullOutput);
```

Output
```
[0, 0, -1, 1, -2]
[1, 4, 9]
```