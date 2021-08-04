package com.wiseassblog.donk.framework

import com.wiseassblog.donk.DonkToken
import com.wiseassblog.donk.TokenType
import com.wiseassblog.donk.scanner.DonkHandWrittenScanner
import com.wiseassblog.donk.scanner.IScanner
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Paths

/**
 * This class contains the main method which we will use to interact with
 * the source code.
 *
 */
fun main(args: Array<String>) {
    when {
        args.size == 1 -> readSource(args[0])
        args.size == 0 -> runPrompt()
        else -> {
            println("Your arguments are invalid.")
            System.exit(64)
        }
    }
}

fun readSource(path: String) {
   val source = String(
       Files.readAllBytes(Paths.get(path)),
           Charset.defaultCharset()
       )

    runSource(source)
}

fun runSource(source: String) {
    val scanner = DonkHandWrittenScanner()
    val scannerResult = scanner.getTokens(source)
}

fun runPrompt() {
    val streamReader = BufferedReader(InputStreamReader(System.`in`))

    while (true) {
        println("> ")
        val line = streamReader.readLine()
        if (line == null) break;
        runSource(line)
    }
}


