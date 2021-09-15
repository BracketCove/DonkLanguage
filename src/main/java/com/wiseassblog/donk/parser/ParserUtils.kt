package com.wiseassblog.donk.parser

import com.wiseassblog.donk.DonkToken
import com.wiseassblog.donk.TokenType


enum class ReturnType {
    NONE,
    BOOLEAN,
    STRING,
    DOUBLE
}

internal fun getErrorPair(message: String, index: Int): Pair<BaseStmt, Int> {
    return Pair(
        ErrorStmt(
            listOf(ParserException(message))
        ),
        index
    )
}


internal val Pair<BaseStmt, Int>.toErrorPair: Pair<BaseStmt, Int>
    get() = Pair(
        this.first,
        this.second + 1
    )

internal val TokenType.toReturnType: ReturnType
    get() = when (this) {
        TokenType.TYPE_DOUBLE -> ReturnType.DOUBLE
        TokenType.TYPE_STRING -> ReturnType.STRING
        TokenType.TYPE_BOOLEAN -> ReturnType.BOOLEAN
        else -> ReturnType.NONE
    }


/**
 * Helper function to match one type against multiple other types.
 * Since we have a list of DonkTokens it becomes easier to just pass the token in
 */
internal fun matchTypes(token: DonkToken, vararg comparisons: TokenType): Boolean {
    comparisons.forEach { comparison ->
        if (token.type == comparison) return true
    }
    return false
}

internal fun DonkToken.isOperator() = when (this.type) {
    TokenType.PLUS,
    TokenType.MINUS,
    TokenType.SLASH,
    TokenType.ASTERISK,
    TokenType.EQUAL_EQUAL,
    TokenType.EXCLM_EQUAL,
    TokenType.LESS,
    TokenType.LESS_EQUAL,
    TokenType.GREATER,
    TokenType.GREATER_EQUAL -> true
    else -> false
}