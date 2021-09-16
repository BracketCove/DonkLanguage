import com.wiseassblog.donk.DonkToken
import com.wiseassblog.donk.TokenType
import com.wiseassblog.donk.parser.*
import org.junit.jupiter.api.Test

class ParserStatementTests {

    val parser = HandWrittenDonkParser()

    /**
     * 1. Set up the test arguments
     * 2. Figure out the expected result
     * 3. Test the unit
     * 4. Assert the results
     */
    @Test
    fun parseReturnStmt() {
        val testOne = listOf<DonkToken>(
            DonkToken(
                "instr",
                TokenType.INSTR
            ),
            DonkToken(
                "stub",
                TokenType.IDENTIFIER
            ),
            DonkToken(
                "(",
                TokenType.LEFT_PAREN
            ),
            DonkToken(
                ")",
                TokenType.RIGHT_PAREN
            ),
            DonkToken(
                ":",
                TokenType.COLON
            ),
            DonkToken(
                "String",
                TokenType.TYPE_STRING
            ),
            DonkToken(
                "{",
                TokenType.LEFT_BRACE
            ),
            DonkToken(
                "return",
                TokenType.RETURN
            ),
            DonkToken(
                "DERP",
                TokenType.LITERAL_STRING,
                "DERP"
            ),
            DonkToken(
                ";",
                TokenType.SEMICOLON
            ),
            DonkToken(
                "}",
                TokenType.RIGHT_BRACE
            )
        )

        val result = parser.parse(testOne)

        val expectedResult = FunctionStmt(
            testOne[1],
            ReturnType.STRING,
            FunctionExpr(
                emptyList(),
                listOf(
                    ReturnStmt(
                        LiteralExpr(
                            DonkToken(
                                "DERP",
                                TokenType.LITERAL_STRING,
                                "DERP"
                            )
                        )
                    )
                )
            )
        )

        assert(
            result is ParserResult.Success &&
                    result.tokens.contains(expectedResult)
        )
    }

    @Test
    fun parseWhileStmt() {
        val testOne = listOf<DonkToken>(
            DonkToken(
                "instr",
                TokenType.INSTR
            ),
            DonkToken(
                "stub",
                TokenType.IDENTIFIER
            ),
            DonkToken(
                "(",
                TokenType.LEFT_PAREN
            ),
            DonkToken(
                ")",
                TokenType.RIGHT_PAREN
            ),
            DonkToken(
                "{",
                TokenType.LEFT_BRACE
            ),
            DonkToken(
                "while",
                TokenType.WHILE
            ),
            DonkToken(
                "(",
                TokenType.LEFT_PAREN
            ),
            DonkToken(
                "true",
                TokenType.TRUE
            ),
            DonkToken(
                ")",
                TokenType.RIGHT_PAREN
            ),
            DonkToken(
                "{",
                TokenType.LEFT_BRACE
            ),
            DonkToken(
                "DERP",
                TokenType.LITERAL_STRING
            ),
            DonkToken(
                ";",
                TokenType.SEMICOLON
            ),
            DonkToken(
                "}",
                TokenType.RIGHT_BRACE
            ),
            DonkToken(
                "}",
                TokenType.RIGHT_BRACE
            )
        )

        val result = parser.parse(testOne)

        val expectedResult = FunctionStmt(
            testOne[1],
            ReturnType.NONE,
            FunctionExpr(
                emptyList(),
                listOf(
                    WhileStmt(
                        BooleanExpr(
                            DonkToken(
                                "true",
                                TokenType.TRUE
                            )
                        ),
                        BlockStmt(
                            listOf(
                                ExprStmt(
                                    LiteralExpr(
                                        DonkToken(
                                            "DERP",
                                            TokenType.LITERAL_STRING
                                        )
                                    )
                                )
                            )
                        )
                    )
                )
            )
        )

        assert(
            result is ParserResult.Success &&
                    result.tokens.contains(expectedResult)
        )
    }

}