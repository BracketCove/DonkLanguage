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