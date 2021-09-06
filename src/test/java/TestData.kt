import com.wiseassblog.donk.DonkToken
import com.wiseassblog.donk.TokenType

val testValDec = listOf(
    DonkToken(
        "val",
        TokenType.VAL
    ),
    DonkToken(
        "thing",
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
        "=",
        TokenType.EQUAL
    ),

    DonkToken(
        "HelloWorld",
        TokenType.LITERAL_STRING
    ),

    DonkToken(
        ";",
        TokenType.SEMICOLON
    )
)

val testVarDec = listOf(
    DonkToken(
        "var",
        TokenType.VAR
    ),
    DonkToken(
        "thing",
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
        "=",
        TokenType.EQUAL
    ),

    DonkToken(
        "HelloWorld",
        TokenType.LITERAL_STRING
    ),

    DonkToken(
        ";",
        TokenType.SEMICOLON
    )
)