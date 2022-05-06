### PushStream
`PushStream` is an eagerly evaluated, re-usable stream. It continues to push its outputs from one stage to the next until it runs out.

```java
PushStream pushStream = PushStream.fromRange(0, 10);
List<Integer> pushOutput = pushStream.flatMap(x -> PushStream.of(-x * x, x * x)).limit(10).toList();

//Output: [0, 0, -1, 1, -4, 4, -9, 9, -16, 16]
```

#### Supported Operations
- Initialization
    - `fromList`, `of`, `concat`, `fromRange`
- Debugging
    - `peek`
- Transformation
    - `map`, `flatMap`, `filter`, `sorted`
    - `limit`, `skip`
- Terminal
    - `count`, `fold`, `sum`
    - `min`, `max`
    - `toList`
- See tests file examples

### PullStream
`PullStream` is a lazily evaluated, single-use stream. Each stage only requests for more information from upstream stages if absolutely necessary. 
- This makes it possible to work with infinitely large sequences, as long as we use a stage that limits the size of the output (e.g. `stream.limit(10)`). 
- Certain stages like `sorted()`, `sum()`, `count()`, etc. will run forever if you attempt to call it on an infinite stream, since it requires knowing all data before it can return an answer
    - TODO: pass some metadata between stages to hint to the user that they shouldn't try to call such methods, or throw an exception

```java
PullStream counter = PullStream.generator(1, x -> x + 1);
int ans = counter.limit(5).fold(0, (a, b) -> a * 10 + b);
assertEquals(12345, ans);
```

#### Supported Operations
- Initialization
    - `fromList`, `fromRange`, `generator`
- Transformation
    - `map`, `flatMap`, `filter`, `sorted`
    - `limit`, `takeWhile`, `skip`
- Terminal
    - `count`, `fold`, `reduce`
    - `min`, `max`
    - `toList`
- See tests file examples