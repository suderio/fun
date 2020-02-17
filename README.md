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

"Hello $name" {name: "world!"};
```

### Labels
```
a: 1
```
```
b: {1, 2}
```
```
c: {a: 1, b: 2}
```
```
b {0} 
> 1
```
```
c {a} 
> 1
```

### Functions

#### Factorial using the any (`*`) element
```
fact: {
  0: 1,
  1: 1,
  *: this {0' - 1}
}
fact {3} 
> 6
```

#### Sum of two numbers: `n | it` is the idiom for the nth element of the argument array
```
sum: {
  *: it {0} + it {1}
}
sum {1, 2}
> 3
```

#### Expressions with unbounded labels define functions (surrounded by `[]`)
```
aSum: [a + b]
aSum {1, 2} 
> 3

aSum {a: 1, b: 2}
> 3
```

#### Maps
```
{2, 3} fact
> {2, 6}
{1, 2} sum
> null
{{1, 2}, {1, 3}} sum
> {2, 5}
```

#### Filters
```
{a: 1, b: 2, c: 3} {a, b} 
> {a: 1, b: 2}

{a, b} {a: 1, b: 2, c:3} 
> {}

{a: 1, b: 3} {a: 1, b: 2, c:3} 
> {a: 1}
```

### Stdin, Stdout, Stderr
```
<< "Hello World!"  
> "Hello World!"

<< "Hello ${name}" {name} >>
> World!
> Hello World!

<!< "Error!"
> "Error!"
```

### Everything together

```
a: {1, 2, 3}

a {}
> {1, 2, 3}

a {0}
> 1

a {0, 1}
> {1, 2}

a {-1}
> 3

a {1, -1}
> {2, 3}

a {-1, 1}
> {3, 2}

lenght a
> 3

lenght: {true: 1, false: 1 + this it {1, *}} {1' = null};
lenght: 1' = null ? { 1, 1 + this it {1, *}};

fib: {
    0: 0,
    1: 1,
    2: 1,
    *: this (0' - 1) + this (0' - 2)
}

lenght fib
> 4

x: {a: 1, b: 2}

x {}
> {a: 1, b: 2}

x {a}
> 1

x {a, c}
> {1, 3}

s: "abc"

s {}
> "abc"

s {0}
> "a"

s {0, 1}
> "ab"
```

### Syntatic Sugar

```
it {a}  -> a'

it {0} -> 0'
```

### Keywords
- this
- it
- null
- true
- false

### Operators

- arithm: + - * / % ( )
- logic: < > <= >= <> = & && | || ~ (not) ^ (xor)
- string: $ (string substitution) 
- language: { } [ ] ' # (comment) : , ; << >> <\!< @ ?
- \ (latex strings) 

### Imports
```
- someImport: fun >> @/someFile.fun
- anotherImport: >> fun @http://github.com/someRepo/someFile.fun
- iso-8891: (fun >> @strings.fun) iso-8891
```

### Files
```
- someString: iso-8891 >> @/someFile.txt
```

### Namespace

File based namespace: At each file you define (rename) the imports, and each valid
fun file is a Dictionary. By filtering the keys you can import only one element of
the file.


