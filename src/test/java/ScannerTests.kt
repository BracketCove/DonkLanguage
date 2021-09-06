import com.wiseassblog.donk.DonkToken
import com.wiseassblog.donk.TokenType
import com.wiseassblog.donk.scanner.DonkHandWrittenScanner
import com.wiseassblog.donk.scanner.IScanner
import com.wiseassblog.donk.scanner.ScannerResult
import org.junit.jupiter.api.Test

class ScannerTests {

    val scanner: IScanner = DonkHandWrittenScanner()

    @Test
    fun testNumberStatement() {
        val testOne = "val blin = 128;"

        val first = DonkToken(
            "val",
            TokenType.VAL,
            Unit,
            1
        )

        val second = DonkToken(
            "blin",
            TokenType.IDENTIFIER,
            Unit,
            1
        )

        val third = DonkToken(
            "=",
            TokenType.EQUAL,
            Unit,
            1
        )

        val fourth = DonkToken(
            "128",
            TokenType.LITERAL_NUMBER,
            128.0,
            1
        )

        val fifth = DonkToken(
            ";",
            TokenType.SEMICOLON,
            Unit,
            1
        )

        val expectedResult = listOf(first, second, third, fourth, fifth)

        val result = scanner.getTokens(testOne)

        assert(
            result is ScannerResult.Success
                    && result.tokens.containsAll(expectedResult)
        )
    }

    @Test
    fun testStringStatement() {
        val testOne = "val blin = \"128\";"

        val first = DonkToken(
            "val",
            TokenType.VAL,
            Unit,
            1
        )

        val second = DonkToken(
            "blin",
            TokenType.IDENTIFIER,
            Unit,
            1
        )

        val third = DonkToken(
            "=",
            TokenType.EQUAL,
            Unit,
            1
        )

        val fourth = DonkToken(
            "128",
            TokenType.LITERAL_STRING,
            "128",
            1
        )

        val fifth = DonkToken(
            ";",
            TokenType.SEMICOLON,
            Unit,
            1
        )

        val expectedResult = listOf(first, second, third, fourth, fifth)

        val result = scanner.getTokens(testOne)

        assert(
            result is ScannerResult.Success
                    && result.tokens.containsAll(expectedResult)
        )
    }


    @Test
    fun testException() {
        val testIllegalChar = "&"

        val result = scanner.getTokens(testIllegalChar)

        assert(result is ScannerResult.Error)
    }

    @Test
    fun testSingleTokens() {
        val testOne = "({,-+;/*}):"

        val lp = DonkToken(
            "(",
            TokenType.LEFT_PAREN,
            Unit,
            1
        )
        val lb = DonkToken(
            "{",
            TokenType.LEFT_BRACE,
            Unit,
            1
        )

        val co = DonkToken(
            ",",
            TokenType.COMMA,
            Unit,
            1
        )

        val mi = DonkToken(
            "-",
            TokenType.MINUS,
            Unit,
            1
        )

        val pl = DonkToken(
            "+",
            TokenType.PLUS,
            Unit,
            1
        )

        val se = DonkToken(
            ";",
            TokenType.SEMICOLON,
            Unit,
            1
        )

        val sl = DonkToken(
            "/",
            TokenType.SLASH,
            Unit,
            1
        )


        val ast = DonkToken(
            "*",
            TokenType.ASTERISK,
            Unit,
            1
        )

        val rb = DonkToken(
            "}",
            TokenType.RIGHT_BRACE,
            Unit,
            1
        )

        val rp = DonkToken(
            ")",
            TokenType.RIGHT_PAREN,
            Unit,
            1
        )

        val cl = DonkToken(
            ":",
            TokenType.COLON,
            Unit,
            1
        )


        val expectedResult = listOf(
            lp, lb, co, mi, pl, se, sl, ast, rb, rp, cl
        )

        val result = scanner.getTokens(testOne)

        assert(
            result is ScannerResult.Success &&
                    result.tokens.containsAll(expectedResult)
        )
    }

    @Test
    fun testComment() {
        val testOne = "/ //asdfgkhkfnbknjfndlkgnh"

        val sl = DonkToken(
            "/",
            TokenType.SLASH,
            Unit,
            1
        )

        val expectedResult = listOf(sl)

        val result = scanner.getTokens(testOne)

        assert(
            result is ScannerResult.Success &&
                    result.tokens.containsAll(expectedResult)
        )
    }

    @Test
    fun testNumber() {
        val testOne = "1234567.0"

        val number = DonkToken(
            "1234567.0",
            TokenType.LITERAL_NUMBER,
            1234567.0,
            1
        )

        val result = scanner.getTokens(testOne)

        assert(
            result is ScannerResult.Success
                    && result.tokens[0].value == number.value
                    && result.tokens[0].type == number.type
                    && result.tokens[0].literal is Double
                    && result.tokens[0].literal == number.literal
        )
    }

    @Test
    fun testString() {
        val testOne = "\"What's up cousins оу блин 12343 !#@@%@%@6\""

        val expectedResultString = "What's up cousins оу блин 12343 !#@@%@%@6"

        val string = DonkToken(
            expectedResultString,
            TokenType.LITERAL_STRING,
            expectedResultString,
            1
        )

        val result = scanner.getTokens(testOne)

        assert(
            result is ScannerResult.Success
                    && result.tokens[0].value == string.value
                    && result.tokens[0].type == string.type
                    && result.tokens[0].literal is String
                    && result.tokens[0].literal == string.literal
        )
    }


    @Test
    fun testIdentifier() {
        val testOne = "vali vari forelse blin"

        val first = DonkToken(
            "vali",
            TokenType.IDENTIFIER,
            Unit,
            1
        )

        val second = DonkToken(
            "vari",
            TokenType.IDENTIFIER,
            Unit,
            1
        )

        val third = DonkToken(
            "forelse",
            TokenType.IDENTIFIER,
            Unit,
            1
        )

        val fourth = DonkToken(
            "blin",
            TokenType.IDENTIFIER,
            Unit,
            1
        )

        val expectedResult = listOf(first, second, third, fourth)

        val result = scanner.getTokens(testOne)

        assert(
            result is ScannerResult.Success
                    && result.tokens.containsAll(expectedResult)

        )
    }

    @Test
    fun testSingleOrDoubleTokens() {
        val testOne = "! = < > != == >= <="

        val first = DonkToken(
            "!",
            TokenType.EXCLM,
            Unit,
            1
        )
        val second = DonkToken(
            "=",
            TokenType.EQUAL,
            Unit,
            1
        )

        val third = DonkToken(
            "<",
            TokenType.LESS,
            Unit,
            1
        )

        val fourth = DonkToken(
            ">",
            TokenType.GREATER,
            Unit,
            1
        )

        val fifth = DonkToken(
            "!=",
            TokenType.EXCLM_EQUAL,
            Unit,
            1
        )

        val sixth = DonkToken(
            "==",
            TokenType.EQUAL_EQUAL,
            Unit,
            1
        )

        val seventh = DonkToken(
            ">=",
            TokenType.GREATER_EQUAL,
            Unit,
            1
        )

        val eighth = DonkToken(
            "<=",
            TokenType.LESS_EQUAL,
            Unit,
            1
        )

        val expectedResult = listOf(
            first, second, third, fourth, fifth, sixth, seventh, eighth
        )

        val result = scanner.getTokens(testOne)

        assert(
            result is ScannerResult.Success &&
                    result.tokens.containsAll(expectedResult)
        )
    }

    /**
     * Note: Double values large than seven (in front of the decimal, i.e. 9999999.0) are not being converted to proper
     * Double values.
     *
     * Sometimes
     */
    @Test
    fun testBasicInput() {
        print("9999999.0".toDouble())
    }

    /**
     * I need to make sure that I know how to grab substring correctly
     *
     * and val var
     * 012345678910
     */
    @Test
    fun testSubstringInput() {
        print("and val var".substring(8, 11))
    }


    @Test
    fun testKeywords() {
        val testOne = "and else false for if instr null or return super true val var while "

        val first = DonkToken(
            "and",
            TokenType.AND,
            Unit,
            1
        )
        val second = DonkToken(
            "else",
            TokenType.ELSE,
            Unit,
            1
        )

        val third = DonkToken(
            "false",
            TokenType.FALSE,
            Unit,
            1
        )

        val fourth = DonkToken(
            "for",
            TokenType.FOR,
            Unit,
            1
        )

        val fifth = DonkToken(
            "if",
            TokenType.IF,
            Unit,
            1
        )

        val sixth = DonkToken(
            "instr",
            TokenType.INSTR,
            Unit,
            1
        )

        val seventh = DonkToken(
            "null",
            TokenType.NULL,
            Unit,
            1
        )

        val eighth = DonkToken(
            "or",
            TokenType.OR,
            Unit,
            1
        )

        val ninth = DonkToken(
            "return",
            TokenType.RETURN,
            Unit,
            1
        )

        val tenth = DonkToken(
            "super",
            TokenType.SUPER,
            Unit,
            1
        )

        val eleventh = DonkToken(
            "true",
            TokenType.TRUE,
            Unit,
            1
        )

        val twelfth = DonkToken(
            "val",
            TokenType.VAL,
            Unit,
            1
        )

        val thirteenth = DonkToken(
            "var",
            TokenType.VAR,
            Unit,
            1
        )

        val fourteenth = DonkToken(
            "while",
            TokenType.WHILE,
            Unit,
            1
        )

        val expectedResult = listOf(
            first, second, third, fourth, fifth, sixth, seventh, eighth,
            ninth, tenth, eleventh, twelfth, thirteenth, fourteenth
        )

        val result = scanner.getTokens(testOne)



        assert(
            result is ScannerResult.Success &&
                    result.tokens.containsAll(expectedResult)
        )

    }

    @Test
    fun testTypesInput() {
        val source = "String Double Boolean "

        val t1 = DonkToken(
            "String",
            TokenType.TYPE_STRING
        )

        val t2 = DonkToken(
            "Double",
            TokenType.TYPE_DOUBLE
        )

        val t3 = DonkToken(
            "Boolean",
            TokenType.TYPE_BOOLEAN
        )

        val expected = listOf(t1, t2, t3)
        val result = scanner.getTokens(source)

        assert(
            result is ScannerResult.Success &&
                    result.tokens.containsAll(expected)
        )
    }


}