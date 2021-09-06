import com.wiseassblog.donk.DonkToken
import com.wiseassblog.donk.TokenType
import com.wiseassblog.donk.parser.*
import org.junit.jupiter.api.Test

class ParserTests {

    //Test data:
    val binaryExprTokens = listOf<DonkToken>(
        DonkToken(
            "a",
            TokenType.IDENTIFIER
        ),
        DonkToken(
            "+",
            TokenType.COMMA
        ),
        DonkToken(
            "b",
            TokenType.IDENTIFIER
        )
    )

    //a + b binary expression
    val binaryExample =
        BinaryExpr(
            LiteralExpr(
                DonkToken(
                    "a",
                    TokenType.IDENTIFIER
                )
            ),
            DonkToken(
                "+",
                TokenType.COMMA
            ),
            LiteralExpr(
                DonkToken(
                    "b",
                    TokenType.IDENTIFIER
                )
            )
        )


    val parser = HandWrittenDonkParser()

    /**
     * FunctionStmt -> instr LITERAL FunctionExpr
     *
     * FunctionExpr -> parameters* statements*
     */
    @Test
    fun parseFunctionDeclaration() {
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
            FunctionExpr(
                emptyList(),
                emptyList()
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

    @Test
    fun parseBinaryExpression() {
//        val testOne = binaryExprTokens
//
//        val parseResult = parser.parse(testOne)
//
//        val expectedResult = listOf<BaseStmt>(
//            binaryExample
//        )
//
//        assert(
//            parseResult is ParserResult.Success &&
//                    expectedResult.containsAll(parseResult.tokens)
//        )
    }

    @Test
    fun parseUnaryExpression() {

    }



    @Test
    fun parseVariableDeclaration() {

    }

    @Test
    fun parseValueDeclaration() {

    }
}