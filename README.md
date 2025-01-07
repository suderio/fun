# Fun Language - Reference Manual

Welcome to the **Fun Language** reference manual! This guide provides a
comprehensive and user-friendly overview of the language, covering syntax,
operators, data types, and constructs. **Fun Language** is designed to be
expressive, flexible, and easy to use, supporting arithmetic operations,
logic, table manipulation, and more.

---

## Table of Contents

1. [Basic Structure](#basic-structure)
2. [Data Types](#data-types)
3. [Operators](#operators)
4. [Tables](#tables)
5. [Functions and Unary Operators](#functions-and-unary-operators)
6. [Strings and Substitutions](#strings-and-substitutions)
7. [Comments](#comments)

---

## Basic Structure

### File

A **Fun Language** program is a sequence of **expressions**, separated by
semicolons (`;`). Example:

```fun
x : 42;
y : x + 8;
result : y * 2;
```

Every file returns a Table (more about it below) with the result of every
expression and all assigned variables.

### Assignments

A variable is assigned using `:`. The left-hand side is the identifier, and
the right-hand side is an expression. An assignment is also an expression
that returns its right-hand side value.

```fun
name : "John Doe";
age : 30;
```

---

## Data Types

### Supported Literals

1. **Integers**:

   ```fun
   x : 42;
   ```

1. **Decimals**:

   ```fun
   pi : 3.14;
   ```

1. **Strings**:

- **Simple**: Enclosed in double quotes (`"`).
- **Multiline (DocStrings)**: Enclosed in `"""`.

```fun
simpleString : "Hello, World!";
docString : """
    Line 1
    Line 2
""";
```

1. **Booleans**:

   ```fun
   flag : true;
   ```

1. **Null**:

   ```fun
   value : null;
   ```

1. **Special Identifiers**:

- `this`: Refers to the body of a unary operator.
- `it`: The argument of unary operators.

---

## Operators

### Arithmetic

Arithmetic operators always return a number (integer or decimal).

| Operator | Description    | Example  |
| -------- | -------------- | -------- |
| `+`      | Addition       | `3 + 2`  |
| `-`      | Subtraction    | `5 - 1`  |
| `*`      | Multiplication | `4 * 2`  |
| `/`      | Division       | `8 / 4`  |
| `%`      | Modulo         | `5 % 2`  |
| `**`     | Exponentiation | `2 ** 3` |

### Extended Assignment

| Operator | Description                   | Example  |
| -------- | ----------------------------- | -------- |
| `+=`     | Addition and Assignment       | `x += 2` |
| `-=`     | Subtraction and Assignment    | `x -= 1` |
| `*=`     | Multiplication and Assignment | `x *= 3` |
| `/=`     | Division and Assignment       | `x /= 4` |
| `%=`     | Modulo and Assignment         | `x %= 5` |

### Logical

Logical operators always return a Boolean.

| Operator | Description       | Example           |
| -------- | ----------------- | ----------------- |
| `&&`     | Short-circuit AND | `true && false`   |
| `\|\|`   | Short-circuit OR  | `true \|\| false` |
| `&`      | Bitwise AND       | `x & y`           |
| `\|`     | Bitwise OR        | `x \| y`          |
| `^`      | Bitwise XOR       | `x ^ y`           |

### Comparison

Comparison operators always return a Boolean.

| Operator   | Description              | Example  |
| ---------- | ------------------------ | -------- |
| `=`        | Equal to                 | `x = y`  |
| `<>`, `~=` | Not equal to             | `x <> y` |
| `<`        | Less than                | `x < y`  |
| `<=`       | Less than or equal to    | `x <= y` |
| `>`        | Greater than             | `x > y`  |
| `>=`       | Greater than or equal to | `x >= y` |

### Miscellaneous

| Operator | Description         | Example             |
| -------- | ------------------- | ------------------- |
| `$`      | String substitution | `"Hello $0" $ [42]` |
| `..`     | Numeric range       | `1..5`              |
| `.`      | Table filter        | `table.key`         |
| `@`      | Redirect            | `@ stdin`           |
| `?`      | Test                | `(1 = 0) ?`         |
| `??`     | Null Test           | `x ?? 42`           |

---

## Tables

### Construction

Tables are lists of elements or key-value pairs, constructed with square
brackets (`[]`):

```fun
table1 : [1 2 3];
table2 : ["key": "value" "anotherKey": 42];
```

### Concatenation

Commas (`,`) can be used to concatenate elements into tables:

```fun
result : [1 2], [3 4]; #[[1 2] [3 4]]
```

---

## Functions and Unary Operators

### Defining Unary Operators

Custom operators can be defined using `{}`:

```fun
increment : {it + 1};
```

### Invocation

```fun
result : increment 5;  # Result: 6
```

### Recursion

Every operator defines a `this` operator that references itself.

```fun
factorial : {[1 1].it ?? (this(it - 1)) * it};
```

---

## Strings and Substitutions

### Positional Substitution

Use `$N` to substitute values from a list:

```fun
message : "Value: $0, Double: $1" $ [42, 84];
```

### Variable Substitution

Use `${var}` to substitute the value of a declared variable:

```fun
x : 10;
message : "The value is ${x}";
```

---

## Comments

### Single-Line

Start with `#`:

```fun
# This is a single-line comment
```

### Block

Enclosed in `###`:

```fun
###
This is a
block comment.
###
```

---

This manual provides an introductory overview of the **Fun Language**. Explore
its capabilities and build powerful expressions effortlessly! 🚀

## Initial Design Goals

- Scripting language, used to fast development
- Very few types:
  - Null
  - Boolean
  - Number (Integer and Decimal subtypes)
  - Table (String subtype)
  - Function
  - Resource
  - URL
- Variables can hold any type (dynamic type, only values have type, not variables)
- Errors return null; in (almost) any operation with null, null is returned
- Resources are, e.g., files, stdin, stdout, etc.
- Every Operator (i.e., Function) must be able to return a value for any type,
  or null if the value is not supported.
- Extreme orthogonality of the basic operators.

---

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:

```shell script
./mvnw quarkus:dev
```

> **_NOTE:_** Quarkus now ships with a Dev UI, which is available in dev mode only at <http://localhost:8080/q/dev/>.

---

## Packaging and running the application

The application can be packaged using:

```shell script
./mvnw package
```

It produces the `quarkus-run.jar` file in the `target/quarkus-app/` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the
`target/quarkus-app/lib/` directory.

The application is now runnable using `java -jar target/quarkus-app/quarkus-run.jar`.

If you want to build an _über-jar_, execute the following command:

```shell script
./mvnw package -Dquarkus.package.jar.type=uber-jar
```

The application, packaged as an _über-jar_, is now runnable using `java -jar target/*-runner.jar`.

---

## Creating a native executable

You can create a native executable using:

```shell script
./mvnw package -Dnative
```

Or, if you don't have GraalVM installed, you can run the native executable
build in a container using:

```shell script
./mvnw package -Dnative -Dquarkus.native.container-build=true
```

You can then execute your native executable with: `./target/lang-1.0.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult <https://quarkus.io/guides/maven-tooling>.

---

## Related Guides

- Picocli ([guide](https://quarkus.io/guides/picocli)): Develop command line
  applications with Picocli
- YAML Configuration ([guide](https://quarkus.io/guides/config-yaml)): Use
  YAML to configure your Quarkus application

---

## Provided Code

### YAML Config

Configure your application with YAML

[Related guide section...](https://quarkus.io/guides/config-reference#configuration-examples)

The Quarkus application configuration is located in `src/main/resources/application.yml`.

### Picocli Example

Hello and goodbye are civilization fundamentals. Let's not forget it with this
example picocli application by changing the `command` and `parameters`.

[Related guide section...](https://quarkus.io/guides/picocli#command-line-application-with-multiple-commands)

Also for picocli applications the dev mode is supported. When running dev mode,
the picocli application is executed and on press of the Enter key, is restarted.

As picocli applications will often require arguments to be passed on the
commandline, this is also possible in dev mode via:

```shell script
./mvnw quarkus:dev -Dquarkus.args='Quarky'
```
