package com.wiseassblog.donk

enum class TokenType {
    //Single tokens
    LEFT_PAREN, RIGHT_PAREN, LEFT_BRACE, RIGHT_BRACE,
    COMMA, MINUS, PLUS, SEMICOLON, SLASH, ASTERISK,

    //Single or double
    EXCLM, EXCLM_EQUAL,
    EQUAL, EQUAL_EQUAL,
    GREATER, GREATER_EQUAL,
    LESS, LESS_EQUAL,

    //Literals
    IDENTIFIER, STRING, NUMBER,

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



