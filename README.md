# fun

## Examples

### Expressions

```
[1 + 1]
> 2
```

```
[ 2 + [3 * 4]]
> 14
```

### Arrays

```
{1, 2, 3}
```

### Dictionaries

```
{'a': 1, 'b': 2, 'c': 3}
```

### Strings

```
"abc"
> abc
```

```
"""
abc
xpto
"""
> abc
> xpto
```

### Labels
```
a: 1
```
```
b: { 1, 2 }
```
```
c: { a: 1, b: 2}
```
```
b(0)
> 1
```
```
c(a)
> 1
```

### Functions
```
fact: {
  0: 1,
  1: 1,
   : fact(it - 1)
}
fact(3)
> 6
```

```
sum: {
  : it(0) + it(1) 
}
sum(1, 2)
> 3
```

aSum: [a + b]
aSum(1, 2)
> 3
aSum(a: 1, b: 2)
> 3

### Loops



### Everything together

```
a: {1, 2, 3}

a()
> {1, 2, 3}

a(0)
> 1

a(0, 1)
> {1, 2}

a(-1)
> 3

a(1, -1)
> {2, 3}

a(-1, 1)
> {3, 2}

lenght(a)
> 3

fib: {
    0: 0,
    1: 1,
    2: 1,
     : fib[it - 1] + fib[it - 2]
}

lenght(fib)
> 3

x: {a: 1, b: 2}

x()
> {a: 1, b: 2, c: 3}

x(a)
> 1

x(a, c)
> {1, 3}

s: "abc"

s()
> "abc"

s(0)
> "a"

s(0, 1)
> "ab"

