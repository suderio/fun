# fun

## Examples

### Expressions

```
1 + 1
> 2
```

```
2 + (3 * 4)
> 14

4!
> 24

2^3
> 8

"ab" * 3
> "ababab"

"ab" + "c"
> "abc"

"c" + "ab"
> "cab"

{1, 2} + {3}
> {1, 2, 3}

{1, 2} + 3
> {4, 5}

1 + "a"
> "1a"

"1a1" - 1
> "a1"

"1a1" / 1
> "a"

1 - "1a1"
> ""

"ababab" / 3
> {"ab", "cd", "ef"}

"a" / 3
> {"a", "", ""}

"ab" * "cd"
> "acadbcbd"

"abc" / "c"
> "ab"

3 / "a"
> ""
```

### Arrays

```
{1, 2, 3}
```

### Dictionaries

```
{a: 1, 'b': 2, 3: 3, "four": 4, {five}: 5}
```

### Strings

```
"abc"

"""
abc
xpto
"""

{name: "world!"} | "Hello $name"

```

### Labels
```
a: 1
```
```
b: {1, 2 }
```
```
c: {a: 1, b: 2}
```
```
{0} | b
> 1
```
```
{a} | c
> 1
```

### Functions

#### Factorial using the any (`*`) element
```
fact: {
  0: 1,
  1: 1,
  *: {0' - 1} | this
}
{3} | fact
> 6
```

#### Sum of two numbers: `n | it` is the idiom for the nth element of the argument array
```
sum: {
  *: {0} | it + {1} | it 
}
{1, 2} | sum
> 3
```

#### Expressions with unbounded labels define functions (surrounded by `[]`)
```
aSum: [a + b]
{1, 2} | aSum
> 3

{a: 1, b: 2} | aSum
> 3
```

#### Maps
```
fact | {2, 3}
> {2, 6}
sum | {1, 2}
> null
sum | {{1, 2}, {1, 3}}
> {2, 5}

```

#### Filters
```
{a, b} | {a: 1, b: 2, c: 3}
> {a: 1, b: 2}

{a: 1, b: 2, c:3} | {a, b}
> {}

{a: 1, b: 2, c:3} | {a: 1, b: 3}
> {a: 1}
```

### Stdin, Stdout, Stderr
```
"Hello World!" |> 
> "Hello World!"

{name} >| "Hello ${name}" |>
> World!
> Hello World!

"Error!" |&>
> "Error!"
```

### Everything together

```
a: {1, 2, 3}

{} | a
> {1, 2, 3}

{0} | a
> 1

{0, 1} | a
> {1, 2}

{-1} | a
> 3

{1, -1} | a
> {2, 3}

{-1, 1} | a
> {3, 2}

a | lenght
> 3

lenght:  1' = null and 1 or 1 + {1, -1} | it | this

fib: {
    0: 0,
    1: 1,
    2: 1,
    *: 0' - 1 | this + 0' - 2 | this
}

fib | lenght
> 4

x: {a: 1, b: 2}

{} | x
> {a: 1, b: 2}

{a} | x
> 1

{a, c} | x
> {1, 3}

s: "abc"

{} | s
> "abc"

{0} | s
> "a"

{0, 1} | s
> "ab"
```

### Syntatic Sugar

```
0 | * -> 0'

a | * -> a'

0 | it -> 0'
```

### Keywords
- this
- it
- null
- true
- false
