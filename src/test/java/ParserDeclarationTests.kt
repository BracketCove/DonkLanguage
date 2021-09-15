import com.wiseassblog.donk.DonkToken
import com.wiseassblog.donk.TokenType
import com.wiseassblog.donk.parser.*
import org.junit.jupiter.api.Test

class ParserDeclarationTests {

    val parser = HandWrittenDonkParser()

    /**
     * FunctionStmt -> instr LITERAL FunctionExpr
     *
     * FunctionExpr -> parameters* statements*
     */
    @Test
    fun parseFunctionDeclarationNoParamsOrReturn() {
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
            ReturnType.NONE,
            FunctionExpr(
                emptyList(),
                listOf(
                    ExprStmt(
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
    fun parseFunctionDeclarationWithReturn() {
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
                    ExprStmt(
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
    fun parseFunctionDeclarationWithParamsNoReturn() {
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
                "a",
                TokenType.IDENTIFIER
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
                ",",
                TokenType.COMMA
            ),
            DonkToken(
                "b",
                TokenType.IDENTIFIER
            ),
            DonkToken(
                ":",
                TokenType.COLON
            ),
            DonkToken(
                "Boolean",
                TokenType.TYPE_BOOLEAN
            ),
            DonkToken(
                ",",
                TokenType.COMMA
            ),
            DonkToken(
                "c",
                TokenType.IDENTIFIER
            ),
            DonkToken(
                ":",
                TokenType.COLON
            ),
            DonkToken(
                "Double",
                TokenType.TYPE_DOUBLE
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
            ReturnType.NONE,
            FunctionExpr(
                listOf(
                    ParamExpr(
                        DonkToken(
                            "a",
                            TokenType.IDENTIFIER
                        ),
                        TokenType.TYPE_STRING
                    ),
                    ParamExpr(
                        DonkToken(
                            "b",
                            TokenType.IDENTIFIER
                        ),
                        TokenType.TYPE_BOOLEAN
                    ),
                    ParamExpr(
                        DonkToken(
                            "c",
                            TokenType.IDENTIFIER
                        ),
                        TokenType.TYPE_DOUBLE
                    )
                ),
                listOf(
                    ExprStmt(
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
    fun parseValDeclaration() {
        val result = parser.parse(testValDec)

        val expectedResult = ValStmt(
            testValDec[1],
            testValDec[3],
            LiteralExpr(
                testValDec[5]
            )
        )

        assert(
            result is ParserResult.Success &&
                    result.tokens.contains(expectedResult)
        )
    }

    @Test
    fun parseVarDeclaration() {
        val result = parser.parse(testVarDec)

        val expectedResult = VarStmt(
            testVarDec[1],
            testVarDec[3],
            LiteralExpr(
                testVarDec[5]
            )
        )

        assert(
            result is ParserResult.Success &&
                    result.tokens.contains(expectedResult)
        )
    }
}