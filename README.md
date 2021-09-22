# Donk Language

## FAQ
**Why?**
This language is primarily a learning project intended for me to understand how language and compiler development works.
I am a self-taught developer, and a personal goal of mine is to fill in knowledge gaps that I may have due to not being
able to afford a post secondary education. I spent the first seven years of my career simply writing high-level JVM
language code, and as a result I didn't understand much about low level computing or languages.

**What features does the language have?**
Please see the features/progress section below.

**Will you teach/explain any of this?**
Absolutely, but not until I have created a fully functioning compiler. See features/progress for more info.

**Where can I learn about language/compiler/interpreter development?**
Honestly, it was very hard to find good learning resources. Most were either too broad, used too many libraries to hide
things that I wanted to implement myself, or created trivial languages. I found "Crafting Interpreters" by Bob Nystrom
to be the best resource around. I used his book as a roadmap, but my own language and the its implementation is quite 
different. 

## Features/Progress
As of writing this, I have completed the Tokenizer and Parser (I still need a broader test suite for edge cases though).
The next step is to create a compiler, or possibly interpreter with the intention of ultimately producing executable
code for WASM (Web Assembly). Since the Tokenizer and Parser are written in Kotlin, most likely the compiler will be
written in Kotlin as well.

### Current Language Features:
* Three basic Types: String, Number, Boolean
* Top level functions with optional return types
* Typed parameter lists
* Four basic arithmetic operators "+, -, *, -"
* Classic logical operators such as "<, <=, ==, !=, ..."
* Variables, but only allowable within function declarations
* Values (immutable constants) which are allowable Top level or within function declarations
* Variable reassignment
* While loop

### Planned Language Features:
* For loop
* Tuples (I don't plan to add classes, but Tuples would be nice)
* Comments, probably // and /**/ style
* and, or

### Language Specification:
I'll give a brief summary of what the language looks like here, but you'll want to look at TokenType.kt, BaseStmt.kt, 
and BaseExpr.kt in order to see the specification programatically.

## Data Types
### Boolean:
```true```

```false```

### Double:


```13.37 //Double, May be +/-```

### String:

```"" //empty string```

```"Hello Donk"```

```"1337" //string number```

## Expressions

### Arithmetic:
```+ - * /```

### Logical Binary:
We will not allow comparisons of different types; 
they will always return false.

```< > <= >= == !=```

### Logical Unary:
```!```

```and```

```or```

## Declarations
### Values
Values may not change after assignment and are allowed Top-Level

```val greeting: String = "Привет";```

```val number: Number = 1337;```

```val bool: Boolean = false;```

### Variables
Variables may  change after assignment, and are not allow Top-Level (i.e. no global variables)

```var greeting: String = "Привет";```

```var number: Number = 1337;```

```var bool: Boolean = false;```

### Functions:
Functions are refered to as Instructions for human legibility.

```instr someFunction(param1: String, param2: Boolean): Double {...}```

Returns types can be ommitted, and param list may be empty.

## Control Flow

### if statement:
```
val condition = false;
if (condition) {...} else {...} 
```

### while loop:


```
while (someNumber < 10) {

    someNumber = someNumber + 1;
}
```

## Function Call:
```
someFunction(arg1, arg2);
```

## Resources
To ask questions or to watch me build this language live, go here:
[Live code playlist](https://youtube.com/playlist?list=PLEVlop6sMHCp8k84FRedHLnkwOgRCFr5K)

Follow me on (twitter)[https://twitter.com/wiseAss301]
