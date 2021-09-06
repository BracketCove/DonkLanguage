package com.wiseassblog.donk.parser

import com.wiseassblog.donk.DonkToken
import com.wiseassblog.donk.TokenType
import com.wiseassblog.donk.common.isAtEnd

class HandWrittenDonkParser() : IParser {
    override fun parse(tokens: List<DonkToken>): ParserResult {
        var current = 0

        val parserErrorList = mutableListOf<ParserException>()
        val statementList = mutableListOf<BaseStmt>()

        while (!current.isAtEnd(tokens.size)) {
            val parse = parseDeclaration(current, tokens)

            current = parse.second

            when (parse.first) {
                is ErrorStmt -> {
                    parserErrorList.addAll((parse.first as ErrorStmt).errors)
                }

                else -> statementList.add(parse.first)
            }
        }

        return if (parserErrorList.isEmpty()) {
            ParserResult.Success(statementList)
        } else ParserResult.Error(parserErrorList)
    }

    /**
     * Check for Declarations ->
     *              | Function Declaration
     *              | Var declaration
     *              | Val declaration
     *              | Statement
     *              ;
     *
     * If Func, Var, or Val not found, parse for Statement
     */
    internal fun parseDeclaration(
        current: Int,
        tokens: List<DonkToken>
    ): Pair<BaseStmt, Int> {
        val token = tokens[current]
        when (token.type) {
            TokenType.INSTR -> {
                val parseFunDec: Pair<BaseStmt, Int> = functionDeclaration(current, tokens)
                return Pair(parseFunDec.first, current + parseFunDec.second)
            }

            TokenType.VAL, TokenType.VAR -> {
                val parseFunDec: Pair<BaseStmt, Int> = valOrVarDeclaration(current, tokens)
                return Pair(parseFunDec.first, current + parseFunDec.second)
            }
        }

        return parseStatement(
            current,
            tokens
        )
    }

    /**
     * Function Declaration -> "instr" IDENTIFIER "(" [ParameterList*] ")"
     *      "{" [Statements*]  \[ReturnStatement\] "}" ;
     *
     * Note assume first token is INSTR
     */
    private fun functionDeclaration(
        current: Int,
        tokens: List<DonkToken>
    ): Pair<BaseStmt, Int> {
        val ID_POS = current + 1
        val L_PAREN_POS = current + 2

        if (tokens[ID_POS].type != TokenType.IDENTIFIER) return Pair<BaseStmt, Int>(
            ErrorStmt(
                listOf(ParserException("Expected IDENTIFIER at $ID_POS, instead got ${tokens[ID_POS].type}"))
            ),
            ID_POS
        )

        if (tokens[L_PAREN_POS].type != TokenType.LEFT_PAREN) return Pair<BaseStmt, Int>(
            ErrorStmt(
                listOf(ParserException("Expected LEFT_PAREN at $L_PAREN_POS, instead got ${tokens[L_PAREN_POS].type}"))
            ),
            L_PAREN_POS
        )

        //either R_PAREN or Param List
        val THIRD_POS = current + 3
        val params = mutableListOf<DonkToken>()
        val hasParams: Boolean

        when (tokens[THIRD_POS].type) {
            TokenType.RIGHT_PAREN -> hasParams = false
            TokenType.IDENTIFIER -> {
                //TODO: include commas in params list then filter them out to have correct index
                hasParams = true
            }
            else -> {
                hasParams = false
                Pair(
                    listOf(
                        ParserException(
                            "Expected RIGHT_PAREN or IDENTIFIER at $THIRD_POS, " +
                                    "instead got ${tokens[THIRD_POS].type}"
                        )
                    ),
                    THIRD_POS
                )
            }

        }

        val L_BRACE_POS: Int
        if (hasParams) {
            //expect R_PAREN the L_BRACE
            L_BRACE_POS = current + params.size
        } else {
            //expect left brace next
            L_BRACE_POS = current + 4
        }

        val blockStmt: BlockStmt
        val BLOCK_POS = current + 5

        if (tokens[L_BRACE_POS].type != TokenType.LEFT_BRACE) return Pair(
            ErrorStmt(
                listOf(
                    ParserException("Expected LEFT_BRACE at $L_BRACE_POS, instead got ${tokens[L_BRACE_POS].type}")
                )
            ),
            L_BRACE_POS
        ) else {
            //parse block
            val parseBlockStmt: Pair<BaseStmt, Int> = parseBlock(
                BLOCK_POS,
                tokens
            )

            when (parseBlockStmt.first) {
                is BlockStmt -> return Pair(
                    FunctionStmt(
                        tokens[ID_POS],
                        FunctionExpr(
                            params,
                            (parseBlockStmt.first as BlockStmt).statements
                        )
                    ),
                    //6 comes from INSTR IDENTIFIER "(" ")" "{" "}"
                    current + 6 + params.size + parseBlockStmt.second
                )

                else -> return Pair(
                    ErrorStmt(
                        listOf(
                            ParserException("Expected BLOCK STATEMENT at $BLOCK_POS, instead got ${tokens[BLOCK_POS].type}")
                        )
                    ),
                    current + 6 + params.size + parseBlockStmt.second
                )
            }
        }
    }

    private fun valOrVarDeclaration(
        current: Int,
        tokens: List<DonkToken>
    ): Pair<BaseStmt, Int> {
        val isVar = (tokens[current].type == TokenType.VAR)
        var index = current + 1
        val name: DonkToken
        val type: DonkToken
        val expr: BaseExpr
        //expect IDENTIFIER

        if (tokens[index].type == TokenType.IDENTIFIER)
            name = tokens[index]
        else return Pair(
            ErrorStmt(
                listOf(
                    ParserException(
                        "Expected IDENTIFIER but recieved ${tokens[index].type}"
                    )
                )
            ),
            index
        )

        //expect semicolon:
        index++

        if (tokens[index].type != TokenType.COLON) return Pair(
            ErrorStmt(
                listOf(
                    ParserException(
                        "Expected COLON but recieved ${tokens[index].type}"
                    )
                )
            ),
            index
        )

        //Expected Type
        index++

        when (tokens[index].type) {
            TokenType.TYPE_STRING,
            TokenType.TYPE_DOUBLE,
            TokenType.TYPE_BOOLEAN -> type = tokens[index]
            else -> return Pair(
                ErrorStmt(
                    listOf(
                        ParserException(
                            "Expected COLON but recieved ${tokens[index].type}"
                        )
                    )
                ),
                index
            )
        }

        //Expect equals
        index++

        if (tokens[index].type != TokenType.EQUAL) return Pair(
            ErrorStmt(
                listOf(
                    ParserException(
                        "Expected EQUAL but recieved ${tokens[index].type}"
                    )
                )
            ),
            index
        )

        //Expect an expression
        index++

        val exprResult = parseExpression(index, tokens)

        if (exprResult.first is ErrorStmt) return Pair(
            ErrorStmt(
                (exprResult.first as ErrorExpr).errors
            ),
            index + exprResult.second
        )

        if (isVar) return Pair(
            VarStmt(
                name,
                type,
                (exprResult.first as ExprStmt).expr
            ),
            index + exprResult.second
        ) else return Pair(
            ValStmt(
                name,
                type,
                (exprResult.first as ExprStmt).expr
            ),
            index + exprResult.second
        )
    }

    /**
     * //Assume we are about to parse some series of statements or expressions
     */
    private fun parseBlock(current: Int, tokens: List<DonkToken>): Pair<BaseStmt, Int> {
        return parseDeclaration(current, tokens)
    }

    internal fun parseStatement(
        current: Int,
        tokens: List<DonkToken>
    ): Pair<BaseStmt, Int> {

        while ()
        when (tokens[current].type) {
            TokenType.LITERAL_STRING -> return Pair(
                LiteralExpr
            )
        }

        val parseExpressionResult = parseExpression(current, tokens)
        return Pair<BaseStmt, Int>(parseExpressionResult.first, current + parseExpressionResult.second)
    }

    /**
     * ExpressionStatement -> Expression ";"
     *
     *
     */
    private fun parseExpression(
        current: Int,
        tokens: List<DonkToken>
    ): Pair<BaseStmt, Int> {
        var expr: BaseStmt

        //expect expr
        when (tokens[current].type) {
            TokenType.LITERAL_STRING,
            TokenType.LITERAL_NUMBER -> expr = ExprStmt(LiteralExpr(tokens[current]))
            else -> expr = ErrorStmt(
                listOf(
                    ParserException(
                        "Expected Expr but recieved ${tokens[current].type}"
                    )
                )
            )
        }

        //Expect semicolon
        if (tokens[current + 1].type != TokenType.SEMICOLON) expr = ErrorStmt(
            listOf(
                ParserException(
                    "Expected SEMICOLON but recieved ${tokens[current + 1].type}"
                )
            )
        )

        return Pair(
            expr,
            2
        )
    }


    /**
     * Identifier,
     * Operator,
     * Identifier
     */
//    private fun matchBinaryExpression(
//        current: Int,
//        token: DonkToken,
//        tokens: List<DonkToken>
//    ): Pair<BaseExpr, Int> {
//        when (token.type) {
//            //TODO decide how to handle index out of bounds
//            TokenType.IDENTIFIER -> {
//                if (tokens[current + 1].isOperator() && tokens[current + 2].type == TokenType.IDENTIFIER) {
//                    return Pair(
//                        BinaryExpr(
//                            VariableExpr(tokens[current]),
//                            tokens[current + 1],
//                            VariableExpr(tokens[current + 2])
//                        ),
//                        3
//                    )
//                }
//            }
//        }
//
//        return Pair(
//            ErrorExpr(
//                ParserException(
//                    "Unable to parse binary expression"
//                )
//            ),
//            1
//        )
//    }

    private fun DonkToken.isOperator() = when (this.type) {
        TokenType.PLUS,
        TokenType.MINUS,
        TokenType.SLASH,
        TokenType.ASTERISK,
        TokenType.EQUAL_EQUAL,
        TokenType.EXCLM_EQUAL,
        TokenType.LESS,
        TokenType.LESS_EQUAL,
        TokenType.GREATER,
        TokenType.GREATER_EQUAL -> true
        else -> false
    }
}