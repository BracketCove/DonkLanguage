package com.wiseassblog.donk


/**
 *                          Scanner/Lexer/Tokenizer -> Parser ->
 * Source code (String) ->  List<Tokens>            -> Abstract Syntax Tree (AST)
 *
 *
 * Sealed classes are used to represent a set of restricted Types
 * enums are a set of restricted values
 *
 */
enum class TokenType {
    //Single tokens
    LEFT_PAREN, RIGHT_PAREN, LEFT_BRACE, RIGHT_BRACE,
    COMMA, MINUS, PLUS, SEMICOLON, SLASH, ASTERISK,
    COLON,

    //Single or double
    EXCLM, EXCLM_EQUAL,
    EQUAL, EQUAL_EQUAL,
    GREATER, GREATER_EQUAL,
    LESS, LESS_EQUAL,

    //Literals
    IDENTIFIER, LITERAL_STRING, LITERAL_NUMBER,

    //Types
    TYPE_STRING, TYPE_DOUBLE, TYPE_BOOLEAN,

    //Keywords
    AND, ELSE, FALSE, FOR, IF, INSTR, NULL, OR,
    RETURN, SUPER, TRUE, VAL, VAR, WHILE,

    //End of file
    EOF,

    //Skip necessary since I'm not using too much shared mutable state
    SKIP,

    //Since I'm not using global variables, it is easier to represent errors with an actual token instead of using
    //thrown exceptions
    ERROR
}



