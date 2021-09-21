import com.wiseassblog.donk.DonkToken
import com.wiseassblog.donk.TokenType
import com.wiseassblog.donk.parser.*
import org.junit.jupiter.api.Test

class ParserExpressionTests {
    val parser = DonkHandWrittenDonkParser()

    @Test
    fun parseAssExpr() {
        val testOne = listOf<DonkToken>(
            DonkToken(
                "a",
                TokenType.IDENTIFIER,
                "a"
            ),
            DonkToken(
                "=",
                TokenType.EQUAL
            ),
            DonkToken(
                "2",
                TokenType.LITERAL_NUMBER,
                2.0
            )
        )

        val result = parser.parse(testOne)

        val expectedResult = ExprStmt(
            AssignExpr(
                DonkToken(
                    "a",
                    TokenType.IDENTIFIER,
                    "a"
                ),
                LiteralExpr(
                    DonkToken(
                        "2",
                        TokenType.LITERAL_NUMBER,
                        2.0
                    )
                )
            )
        )

        assert(
            result is ParserResult.Success &&
                    result.statements.contains(expectedResult)
        )
    }

    @Test
    fun parseLogicalExpr() {
        val testOne = listOf<DonkToken>(
            DonkToken(
                "1.0",
                TokenType.LITERAL_NUMBER,
                1.0
            ),
            DonkToken(
                "==",
                TokenType.EQUAL_EQUAL
            ),
            DonkToken(
                "1.0",
                TokenType.LITERAL_NUMBER,
                1.0
            )
        )

        val result = parser.parse(testOne)

        val expectedResult = ExprStmt(
            LogicalExpr(
                LiteralExpr(
                    DonkToken(
                        "1.0",
                        TokenType.LITERAL_NUMBER,
                        1.0
                    )
                ),
                DonkToken(
                    "==",
                    TokenType.EQUAL_EQUAL
                ),
                LiteralExpr(
                    DonkToken(
                        "1.0",
                        TokenType.LITERAL_NUMBER,
                        1.0
                    )
                )
            )
        )

        assert(
            result is ParserResult.Success &&
                    result.statements.contains(expectedResult)
        )
    }

    @Test
    fun parseCallExpr() {
        val testOne = listOf<DonkToken>(
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
            )
        )

        val result = parser.parse(testOne)

        val expectedResult = ExprStmt(
            CallExpr(
                DonkToken(
                    "stub",
                    TokenType.IDENTIFIER
                ),
                emptyList()
            )
        )

        assert(
            result is ParserResult.Success &&
                    result.statements.contains(expectedResult)
        )
    }


    @Test
    fun parseCallExprWithArgs() {
        val testOne = listOf<DonkToken>(
            DonkToken(
                "stub",
                TokenType.IDENTIFIER
            ),
            DonkToken(
                "(",
                TokenType.LEFT_PAREN
            ),
            DonkToken(
                "derp",
                TokenType.IDENTIFIER
            ),

            DonkToken(
                ",",
                TokenType.COMMA
            ),
            DonkToken(
                "herp",
                TokenType.LITERAL_STRING
            ),
            DonkToken(
                ",",
                TokenType.COMMA
            ),
            DonkToken(
                "43.0",
                TokenType.LITERAL_NUMBER
            ),
            DonkToken(
                ")",
                TokenType.RIGHT_PAREN
            )
        )

        val result = parser.parse(testOne)

        val expectedResult = ExprStmt(
            CallExpr(
                DonkToken(
                    "stub",
                    TokenType.IDENTIFIER
                ),
                listOf(
                    LiteralExpr(
                        DonkToken(
                            "derp",
                            TokenType.IDENTIFIER
                        )
                    ),
                    LiteralExpr(
                        DonkToken(
                            "herp",
                            TokenType.LITERAL_STRING
                        )
                    ),
                    LiteralExpr(
                        DonkToken(
                            "43.0",
                            TokenType.LITERAL_NUMBER
                        )
                    )
                )
            )
        )

        assert(
            result is ParserResult.Success &&
                    result.statements.contains(expectedResult)
        )
    }

    @Test
    fun parseCallExprWithNestedCall() {
        val testOne = listOf<DonkToken>(
            DonkToken(
                "stub",
                TokenType.IDENTIFIER
            ),
            DonkToken(
                "(",
                TokenType.LEFT_PAREN
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
                ")",
                TokenType.RIGHT_PAREN
            )
        )

        val result = parser.parse(testOne)

        val expectedResult = ExprStmt(
            CallExpr(
                DonkToken(
                    "stub",
                    TokenType.IDENTIFIER
                ),
                listOf(
                    CallExpr(
                        DonkToken(
                            "stub",
                            TokenType.IDENTIFIER
                        ),
                        emptyList()
                    )
                )
            )
        )

        assert(
            result is ParserResult.Success &&
                    result.statements.contains(expectedResult)
        )
    }

    @Test
    fun parseUnary() {
        val testOne = listOf<DonkToken>(
            DonkToken(
                "!",
                TokenType.EXCLM
            ),
            DonkToken(
                "true",
                TokenType.TRUE
            )
        )

        val result = parser.parse(testOne)

        val expectedResult = ExprStmt(
            UnaryExpr(
                DonkToken(
                    "!",
                    TokenType.EXCLM
                ),
                BooleanExpr(
                    DonkToken(
                        "true",
                        TokenType.TRUE
                    )
                )
            )
        )

        assert(
            result is ParserResult.Success &&
                    result.statements.contains(expectedResult)
        )
    }

    @Test
    fun parseGrouping() {
        val testOne = listOf<DonkToken>(
            DonkToken(
                "(",
                TokenType.LEFT_PAREN
            ),
            DonkToken(
                "1",
                TokenType.LITERAL_NUMBER
            ),
            DonkToken(
                ")",
                TokenType.RIGHT_PAREN
            )
        )

        val result = parser.parse(testOne)

        val expectedResult = ExprStmt(
            GroupingExpr(
                LiteralExpr(
                    DonkToken(
                        "1",
                        TokenType.LITERAL_NUMBER
                    )
                )
            )
        )

        assert(
            result is ParserResult.Success &&
                    result.statements.contains(expectedResult)
        )
    }

    @Test
    fun parseBinaryGrouping() {
        val testOne = listOf<DonkToken>(
            DonkToken(
                "(",
                TokenType.LEFT_PAREN
            ),
            DonkToken(
                "1",
                TokenType.LITERAL_NUMBER
            ), DonkToken(
                "+",
                TokenType.PLUS
            ),
            DonkToken(
                "1",
                TokenType.LITERAL_NUMBER
            ),

            DonkToken(
                ")",
                TokenType.RIGHT_PAREN
            )
        )

        val result = parser.parse(testOne)

        val expectedResult = ExprStmt(
            GroupingExpr(
                BinaryExpr(
                    LiteralExpr(
                        DonkToken(
                            "1",
                            TokenType.LITERAL_NUMBER
                        )
                    ),
                    DonkToken(
                        "+",
                        TokenType.PLUS
                    ),
                    LiteralExpr(
                        DonkToken(
                            "1",
                            TokenType.LITERAL_NUMBER
                        )
                    ),
                    Precedence.LOW
                )
            )
        )

        assert(
            result is ParserResult.Success &&
                    result.statements.contains(expectedResult)
        )
    }

    @Test
    fun parseBinaryGroupings() {
        val testOne = listOf<DonkToken>(
            DonkToken(
                "(",
                TokenType.LEFT_PAREN
            ),
            DonkToken(
                "1",
                TokenType.LITERAL_NUMBER
            ), DonkToken(
                "+",
                TokenType.PLUS
            ),
            DonkToken(
                "(",
                TokenType.LEFT_PAREN
            ),
            DonkToken(
                "a",
                TokenType.LITERAL_STRING
            ),
            DonkToken(
                "*",
                TokenType.ASTERISK
            ),
            DonkToken(
                "2",
                TokenType.LITERAL_NUMBER
            ),
            DonkToken(
                ")",
                TokenType.RIGHT_PAREN
            ),
            DonkToken(
                ")",
                TokenType.RIGHT_PAREN
            )
        )

        val result = parser.parse(testOne)

        val expectedResult = ExprStmt(
            GroupingExpr(
                BinaryExpr(
                    LiteralExpr(
                        DonkToken(
                            "1",
                            TokenType.LITERAL_NUMBER
                        )
                    ),
                    DonkToken(
                        "+",
                        TokenType.PLUS
                    ),
                    GroupingExpr(
                        BinaryExpr(
                            LiteralExpr(
                                DonkToken(
                                    "a",
                                    TokenType.LITERAL_STRING
                                )
                            ),
                            DonkToken(
                                "*",
                                TokenType.ASTERISK
                            ),
                            LiteralExpr(
                                DonkToken(
                                    "2",
                                    TokenType.LITERAL_NUMBER
                                )
                            ),
                            Precedence.MEDIUM
                        )
                    ),
                    Precedence.LOW
                )
            )
        )

        assert(
            result is ParserResult.Success &&
                    result.statements.contains(expectedResult)
        )
    }

    @Test
    fun parseLiteralBinary() {
        val testOne = listOf<DonkToken>(
            DonkToken(
                "1",
                TokenType.LITERAL_NUMBER
            ),
            DonkToken(
                "+",
                TokenType.PLUS
            ),
            DonkToken(
                "1",
                TokenType.LITERAL_NUMBER
            )
        )

        val result = parser.parse(testOne)

        val expectedResult = ExprStmt(
            BinaryExpr(
                LiteralExpr(
                    DonkToken(
                        "1",
                        TokenType.LITERAL_NUMBER
                    )
                ),
                DonkToken(
                    "+",
                    TokenType.PLUS
                ),
                LiteralExpr(
                    DonkToken(
                        "1",
                        TokenType.LITERAL_NUMBER
                    )
                ),
                Precedence.LOW
            )
        )

        assert(
            result is ParserResult.Success &&
                    result.statements.contains(expectedResult)
                    && result.statements.size == 1
        )
    }

    @Test
    fun parseIdentifierBinary() {
        val testOne = listOf<DonkToken>(
            DonkToken(
                "a",
                TokenType.IDENTIFIER
            ),
            DonkToken(
                "+",
                TokenType.PLUS
            ),
            DonkToken(
                "b",
                TokenType.IDENTIFIER
            )
        )

        val result = parser.parse(testOne)

        val expectedResult = ExprStmt(
            BinaryExpr(
                LiteralExpr(
                    DonkToken(
                        "a",
                        TokenType.IDENTIFIER
                    )
                ),
                DonkToken(
                    "+",
                    TokenType.PLUS
                ),
                LiteralExpr(
                    DonkToken(
                        "b",
                        TokenType.IDENTIFIER
                    )
                ),
                Precedence.LOW
            )
        )

        assert(
            result is ParserResult.Success &&
                    result.statements.contains(expectedResult)
        )
    }
}