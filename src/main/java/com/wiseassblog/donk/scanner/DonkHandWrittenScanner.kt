package com.wiseassblog.donk.scanner

import com.wiseassblog.donk.DonkToken
import com.wiseassblog.donk.TokenType

/**
 *
 * Hand written Scanner (sometimes referred to as a Lexer and vice versa).
 * @see com.wiseassblog.donk.scanner.IScanner
 */
class DonkHandWrittenScanner() : IScanner {
    override fun getTokens(source: String): ScannerResult {
        var start = 0
        var current = 0
        var line = 1

        val tokenList = mutableListOf<DonkToken>()
        val scannerErrorList = mutableListOf<ScannerException>()

        while (!current.isAtEnd(source.length)) {
            val scan = scanToken(start, current, source)

            //new start position
            start = scan.first

            //new current position
            current = scan.second

            //deal with the token itself
            when (scan.third.type) {
                TokenType.SKIP -> {

                }

                TokenType.ERROR -> {
                    scannerErrorList.add(
                        ScannerException(
                            scan.third.literal.toString()
                        )
                    )
                }

                else -> {
                    tokenList.add(scan.third)
                }
            }
        }



        return if (scannerErrorList.isEmpty()) {
            tokenList.add(DonkToken("eof", TokenType.EOF))
            ScannerResult.Success(tokenList)
        }
        else ScannerResult.Error(scannerErrorList)
    }

    /**
     * Triple<newStart, newCurrent, Token>
     */
    internal fun scanToken(start: Int, current: Int, source: String): Triple<Int, Int, DonkToken> {

        val char = source.get(current)

        //whitespace
        when {
            char.isWhitespace() -> return Triple(start, current + 1, DonkToken(char.toString(), TokenType.SKIP))
        }

        //keywords
        when {
            listOf('a', 'e', 'f', 'n', 'o', 'r', 's', 't', 'w', 'i', 'v').contains(char) -> {
                val scanKeyword = matchKeyword(current, source)
                if (scanKeyword.second.type != TokenType.SKIP)
                    return Triple(start, current + scanKeyword.first, scanKeyword.second)
            }
        }

        //Literals
        when {
            char.isDigit() -> {
                val scanNumber = matchNumber(current, source)
                return Triple(start, current + scanNumber.first, scanNumber.second)
            }

            char == '\"' -> {
                val scanString = matchString(current + 1, source)
                return Triple(start, current + scanString.first, scanString.second)
            }
        }

        //identifiers
        when {
            //note the we have eliminated possible numbers already
            char.isLetterOrDigit() -> {
                val scanId = matchId(current, source)
                return Triple(start, current + scanId.first, scanId.second)
            }
        }

        //Single, double, and comment tokens
        return when (char) {
            '(' -> Triple(start, current + 1, DonkToken(char.toString(), TokenType.LEFT_PAREN))
            ')' -> Triple(start, current + 1, DonkToken(char.toString(), TokenType.RIGHT_PAREN))
            '{' -> Triple(start, current + 1, DonkToken(char.toString(), TokenType.LEFT_BRACE))
            '}' -> Triple(start, current + 1, DonkToken(char.toString(), TokenType.RIGHT_BRACE))
            ',' -> Triple(start, current + 1, DonkToken(char.toString(), TokenType.COMMA))
            '-' -> Triple(start, current + 1, DonkToken(char.toString(), TokenType.MINUS))
            '+' -> Triple(start, current + 1, DonkToken(char.toString(), TokenType.PLUS))
            ';' -> Triple(start, current + 1, DonkToken(char.toString(), TokenType.SEMICOLON))
            '*' -> Triple(start, current + 1, DonkToken(char.toString(), TokenType.ASTERISK))
            '/' -> {
                //returns the offset of the consumed token, and the consumed token
                val scanSlashPair = matchSlash(current + 1, source)
                Triple(start, current + scanSlashPair.first, scanSlashPair.second)
            }

            '!' -> Triple(start, current + 2, matchOperator(char, source.get(current + 1)))
            '=' -> Triple(start, current + 2, matchOperator(char, source.get(current + 1)))
            '<' -> Triple(start, current + 2, matchOperator(char, source.get(current + 1)))
            '>' -> Triple(start, current + 2, matchOperator(char, source.get(current + 1)))


            else -> Triple(start, current + 1, DonkToken("", TokenType.ERROR,"Unable to scan input $char at: $current"))
        }
    }

    private fun matchId(index: Int, source: String): Pair<Int, DonkToken> {
        var counter = 0
        val builder = StringBuilder()

        /*
        Why the run block?
        I want to terminate the loop when I find whitespace, but simply returning the forEach actually justs
        continues the iteration instead of return/stopping it.
         */
        run loop@{
            source.filterIndexed { filter, _ -> filter >= index }
                .forEach { char ->
                    if (char.isLetterOrDigit()) {
                        counter++
                        builder.append(char)
                    } else if (char.isWhitespace()) {
                        return@loop
                    } else {
                        return Pair(
                            counter, DonkToken(
                                builder.toString(),
                                TokenType.ERROR,
                                "Invalid character for Identifier"
                            )
                        )
                    }
                }
        }


        return Pair(counter, DonkToken(builder.toString(), TokenType.IDENTIFIER))
    }

    /**
     * I suspect substring might be an efficient way to do this.
     * Remember that all keywords are expected to have a space after them .
     *
     * returns Triple where:
     *  first = starting index
     *  second = Token (I use skip if it turns out this is probably an identifier but I'm not sure)
     *  third = Boolean if a valid keyword was found
     *
     *  note that offsets are + 1 to ensure next char is a whitespace
     *
     *  note index + offset + 1 was necessary to create appropriate substrings
     *
     */
    private fun matchKeyword(index: Int, source: String): Pair<Int, DonkToken> {
        when (source[index]) {
            'a' -> {
                //AND
                val offset = 4
                var substring = source.substring(index, index + offset)
                if (substring == "and ")
                    return Pair(offset, DonkToken(substring.trim(), TokenType.AND))
            }
            'e' -> {
                //ELSE
                val offset = 5
                var substring = source.substring(index, index + offset)
                if (substring == "else ")
                    return Pair(offset, DonkToken(substring.trim(), TokenType.ELSE))
            }
            'n' -> {
                //NULL
                val offset = 5
                var substring = source.substring(index, index + offset)
                if (substring == "null ")
                    return Pair(offset, DonkToken(substring.trim(), TokenType.NULL))
            }
            'o' -> {
                //OR
                val offset = 3
                var substring = source.substring(index, index + offset)
                if (substring == "or ")
                    return Pair(offset, DonkToken(substring.trim(), TokenType.OR))
            }
            'r' -> {
                //RETURN
                val offset = 7
                var substring = source.substring(index, index + offset)
                if (substring == "return ")
                    return Pair(offset, DonkToken(substring.trim(), TokenType.RETURN))
            }
            's' -> {
                //SUPER
                val offset = 6
                var substring = source.substring(index, index + offset)
                if (substring == "super ")
                    return Pair(offset, DonkToken(substring.trim(), TokenType.SUPER))
            }
            't' -> {
                //TRUE
                val offset = 5
                var substring = source.substring(index, index + offset)
                if (substring == "true ")
                    return Pair(offset, DonkToken(substring.trim(), TokenType.TRUE))
            }
            'w' -> {
                //WHILE
                val offset = 6
                var substring = source.substring(index, index + offset)
                if (substring == "while ")
                    return Pair(offset, DonkToken(substring.trim(), TokenType.WHILE))
            }
            'i' -> {
                //IF
                val offsetIf = 3
                var substring = source.substring(index, index + offsetIf)
                if (substring == "if ")
                    return Pair(offsetIf, DonkToken(substring.trim(), TokenType.IF))

                //INSTR
                val offsetInstr = 6
                substring = source.substring(index, index + offsetInstr)
                if (substring == "instr ")
                    return Pair(offsetInstr, DonkToken(substring.trim(), TokenType.INSTR))
            }

            'f' -> {
                //FOR
                val offsetFor = 4
                var substring = source.substring(index, index + offsetFor)
                if (substring == "for ")
                    return Pair(offsetFor, DonkToken(substring.trim(), TokenType.FOR))

                //FALSE
                val offsetFalse = 6
                substring = source.substring(index, index + offsetFalse)
                if (substring == "false ")
                    return Pair(offsetFalse, DonkToken(substring.trim(), TokenType.FALSE))
            }
            'v' -> {
                //val
                val offset = 4
                var substring = source.substring(index, index + offset)
                if (substring == "val ")
                    return Pair(offset, DonkToken(substring.trim(), TokenType.VAL))
                if (substring == "var ")
                    return Pair(offset, DonkToken(substring.trim(), TokenType.VAR))
            }
        }

        var counter = 0

        run loop@ {
            source.filterIndexed { filter, _ -> filter >= index }
                .forEach { char ->
                    counter++
                    if (char.isWhitespace()) {
                        return@loop
                    }
                }
        }


        return Pair(counter, DonkToken(source, TokenType.SKIP))
    }

    private fun matchNumber(index: Int, source: String): Pair<Int, DonkToken> {
        var counter = 0
        val builder = StringBuilder()

        run loop@{
            source.filterIndexed { filter, _ -> filter >= index }
                .forEach { char ->
                    if (char.isDigit() || char == '.') {
                        counter++
                        builder.append(char)
                    } else return@loop
                }
        }


        ////TODO handle multiple decimals in literal elegantly
        if (builder.count { it == '.' } > 1) return Pair(
            counter, DonkToken(
                builder.toString(),
                TokenType.ERROR,
                "Too many decimal points in number literal"
            )
        )

        //Remove trailing decimal
        if (builder.last() == '.') builder.deleteCharAt(builder.lastIndex)

        return Pair(counter, DonkToken(builder.toString(), TokenType.NUMBER, builder.toString().toDouble()))
    }

    private fun matchString(index: Int, source: String): Pair<Int, DonkToken> {
        //counter starts at 1 to maintain consistency of indexes after eating the first quotation mark
        var counter = 1
        val builder = StringBuilder()

        run loop@{
            source.filterIndexed { filter, _ -> filter >= index }
                .forEach { char ->
                    if (char == '\"') {
                        counter++
                        return@loop
                    } else {
                        counter++
                        builder.append(char)
                    }
                }
        }

        return Pair(counter, DonkToken(builder.toString(), TokenType.STRING, builder.toString()))
    }

    private fun matchSlash(index: Int, source: String): Pair<Int, DonkToken> {
        return when (source.get(index)) {
            //if next char is a slash, this is the start of a comment. Consume until newline
            '/' -> {
                var counter = index
                val builder = StringBuilder()
                run loop@{
                    source.filterIndexed { filter, _ -> filter > index }
                        .forEach { char ->
                            counter++
                            builder.append(char)
                            if (char == '\n') return@loop
                        }
                }

                Pair(counter, DonkToken(builder.toString(), TokenType.SKIP))
            }
            else -> Pair(1, DonkToken('/'.toString(), TokenType.SLASH))
        }
    }


    private fun matchOperator(first: Char, second: Char): DonkToken {
        return when {
            first == '!' && second.isWhitespace() -> DonkToken(first.toString(), TokenType.EXCLM)
            first == '=' && second.isWhitespace() -> DonkToken(first.toString(), TokenType.EQUAL)
            first == '<' && second.isWhitespace() -> DonkToken(first.toString(), TokenType.LESS)
            first == '>' && second.isWhitespace() -> DonkToken(first.toString(), TokenType.GREATER)

            first == '!' && second == '=' -> DonkToken(first.toString() + second.toString(), TokenType.EXCLM_EQUAL)
            first == '=' && second == '=' -> DonkToken(first.toString() + second.toString(), TokenType.EQUAL_EQUAL)
            first == '<' && second == '=' -> DonkToken(first.toString() + second.toString(), TokenType.LESS_EQUAL)
            first == '>' && second == '=' -> DonkToken(first.toString() + second.toString(), TokenType.GREATER_EQUAL)

            else ->
                DonkToken(
                    first.toString() + second.toString(),
                    TokenType.ERROR,
                    "Unable to scan compound token $first$second"
                )


        }
    }

    internal fun Int.isAtEnd(length: Int): Boolean = (this >= length)

}