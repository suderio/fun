# fun

## Exemplos

### Expressões

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




