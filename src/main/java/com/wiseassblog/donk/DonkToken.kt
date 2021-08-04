package com.wiseassblog.donk

data class DonkToken (
    val value: String,
    val type: TokenType,
    val literal: Any = Unit,
    val line: Int = 1
)