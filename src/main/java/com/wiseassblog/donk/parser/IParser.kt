package com.wiseassblog.donk.parser

import com.wiseassblog.donk.DonkToken

interface IParser {
    fun parse(tokens: List<DonkToken>): ParserResult
}

sealed class ParserResult {
    data class Success(val statements: List<BaseStmt>) : ParserResult()

    //Why as list? To catch multiple syntax errors in scanned source at once, instead of one by one.
    data class Error(val exception: List<ParserException>) : ParserResult()
}

class ParserException(message: String) : Exception(message)