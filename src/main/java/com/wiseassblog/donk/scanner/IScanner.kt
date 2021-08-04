package com.wiseassblog.donk.scanner

import com.wiseassblog.donk.DonkToken
import com.wiseassblog.donk.TokenType

/**
 * The purpose of the scanner is to take in the raw source code (either from a file, or through input via an interactive
 * console), and to produce a list of tokens; assuming the source is well formed.
 *
 * Why did I use an interface here? I may end up using a library such as JFlex to generate a scanner later on, so this
 * allows me to change implementations or test things easily.
 */
interface IScanner {
    fun getTokens(source: String) : ScannerResult
}

/**
 * Result Wrapper
 */
sealed class ScannerResult {
    data class Success(val tokens: List<DonkToken>) : ScannerResult()

    //Why as list? To catch multiple syntax errors in scanned source at once, instead of one by one.
    data class Error(val exception: List<ScannerException>) : ScannerResult()
}

class ScannerException(message: String) : Exception(message)