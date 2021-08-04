#Donk Language Specification
Donk is largely based on the Kotlin programming language.
    
##Data Types
###Boolean:
true
false

###Number:

1337 //Integer
13.37 //Decimal
//May be +/-

###String:

"" //empty string

"Hello Donk"

"1337" //string number

##Expressions

###Arithmetic:
+
-
*
/

###Comparison:
//We will not allow comparisons of different types; they will always return false
<
\>
<=
\>=

==
!=

###Logical:
!
and
or

##Statements
//Single statement
"Оу блин";


//Block statement
{
"Оу блин";
123;
}

##Values & Values

##Values
//Values may not change after assignment
val greeting: String = "Привет";
val number: Number = 1337;
val bool: Boolean = false;

##Variables
//Variables may  change after assignment
var greeting: String = "Привет";
var number: Number = 1337;
var bool: Boolean = false;

##Control Flow

###if statement:
val condition = false;
if (condition) {
    //evaluate true block
} else {
    //evaluate false block
}

###while loop:
var condition = false;
var someNumber = 0;
while (someNumber < 10) {
    someNumber = someNumber + 1;
}

##Functions

###Function Call:
someFunction(arg1, arg2);

###Function Definition:
//functions are refered to as Instructions for human understandability
instr someFunction(param1: Type, param2: Type) {
    //block statement
}

###Returning values
//specify the re
instr add(a: Number, b: Number): Type {
    return a + b;
}
   


