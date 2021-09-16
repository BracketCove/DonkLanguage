package com.wiseassblog.donk.parser

import com.wiseassblog.donk.DonkToken
import com.wiseassblog.donk.TokenType
import com.wiseassblog.donk.common.isAtEnd


/**
 * Two main kinds of functions:
 * 1. Parse functions. These are recursive parsing functions which attempt to build semantic elements of the language.
 * 2. Match functions. These simply check if a given Token's Type matches any of a variable number of other Types. This
 * cleans up the code and helps to reduce the number of control statements necessary to make comparisons. They return
 * a boolean which indicates if a match was found.
 *
 * Caller increments by 1, not callee
 *
 */
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
                val parseFunDec: Pair<BaseStmt, Int> = parseFunctionDeclaration(current + 1, tokens)
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

    internal fun parseStatement(
        current: Int,
        tokens: List<DonkToken>
    ): Pair<BaseStmt, Int> {
        var index = current
        when (tokens[index].type) {
            TokenType.RETURN -> {
                index++
                val parseReturn = parseReturnStmt(index, tokens)

                return Pair(
                    parseReturn.first,
                    parseReturn.second + 1
                )
            }

            TokenType.WHILE -> {
                index++
                val parseWhile = parseWhileStmt(index, tokens)

                return Pair(
                    parseWhile.first,
                    parseWhile.second + 1
                )
            }
        }

        val parseExpressionResult = parseExpression(current, tokens)
        return Pair<BaseStmt, Int>(parseExpressionResult.first, current + parseExpressionResult.second)
    }

    /**
     * Function Declaration -> "instr" IDENTIFIER  ParameterList [ReturnType]
     *      BlockStatement ";"
     *
     * Note:
     * - Pair<Boolean if match occurs, new index value>.
     * - For for non-recursive matches, I just increment the index by 1
     */
    private fun parseFunctionDeclaration(
        current: Int,
        tokens: List<DonkToken>
    ): Pair<BaseStmt, Int> {
        var index = current

        val instrName: DonkToken
        val parameterList = mutableListOf<ParamExpr>()
        var returnType: ReturnType
        val stmtList = mutableListOf<BaseStmt>()

        val matchInstr = matchTypes(tokens[index], TokenType.IDENTIFIER)
        if (matchInstr) {
            instrName = tokens[index]
            index++
        } else return getErrorPair(
            "Expected IDENTIFIER at $index, instead got ${tokens[index].type}",
            current
        )

        val parseParams = parseParameterList(
            index,
            tokens
        )

        if (parseParams.first is ErrorStmt) return parseParams.toErrorPair
        else {
            parameterList.addAll(
                (parseParams.first as ParameterListStmt).params
            )

            index = parseParams.second + 1
        }

        //first is return type, second is boolean indicating success/failure
        val parseReturn = parseReturnType(
            index,
            tokens
        )

        if (parseReturn.second) {
            returnType = parseReturn.first
            if (parseReturn.first != ReturnType.NONE) index += 2 //eat the colon and RETURN_TYPE
        } else {
            return getErrorPair(
                "expected COLON and then " +
                        "TYPE_STRING or TYPE_BOOLEAN or TYPE_DOUBLE " +
                        "at $index but got ${tokens[index].type}",
                index
            )
        }


        val parseStmtB = parseBlock(index, tokens)

        if (parseStmtB.first is ErrorStmt) return parseStmtB.toErrorPair
        else {
            stmtList.addAll((parseStmtB.first as BlockStmt).statements)
            index = parseStmtB.second
        }

        return Pair(
            FunctionStmt(
                instrName,
                returnType,
                FunctionExpr(
                    parameterList,
                    stmtList
                )
            ),
            index++
        )
    }

    /**
     * The boolean in the Pair indicates if an unexpected Token was found (i.e. not a COLON or a LEFT_BRACE)
     */
    private fun parseReturnType(current: Int, tokens: List<DonkToken>): Pair<ReturnType, Boolean> {
        var index = current

        when (tokens[index].type) {
            TokenType.COLON -> index++
            TokenType.LEFT_BRACE -> {
                return Pair(
                    ReturnType.NONE,
                    true
                )
            }
            else -> return Pair(
                ReturnType.NONE,
                false
            )
        }

        if (
            matchTypes(
                tokens[index],
                *arrayOf(
                    TokenType.TYPE_STRING,
                    TokenType.TYPE_BOOLEAN,
                    TokenType.TYPE_DOUBLE
                )
            )
        ) return Pair(tokens[index].type.toReturnType, true)
        else return Pair(
            ReturnType.NONE,
            false
        )
    }


    /**
     * "(" \[ParamExpr*\] ")"
     */
    private fun parseParameterList(current: Int, tokens: List<DonkToken>): Pair<BaseStmt, Int> {
        var index = current
        val params = mutableListOf<ParamExpr>()

        val matchParenL = matchTypes(tokens[index], TokenType.LEFT_PAREN)

        if (matchParenL) index++
        else return getErrorPair(
            "Expected LEFT_PAREN at $current, instead got ${tokens[index].type}",
            index
        )

        val matchParenR = matchTypes(tokens[index], TokenType.RIGHT_PAREN)

        if (matchParenR) return Pair(
            ParameterListStmt(
                emptyList()
            ),
            index
        )

        while (tokens[index].type != TokenType.RIGHT_PAREN) {
            if (index >= tokens.size) return getErrorPair("Expected Right Paren but reached EOF", index)
            if (matchTypes(tokens[index], TokenType.COMMA)) {
                index++
                continue
            }

            val parseParam = parseParamExpr(index, tokens)

            //TODO fix this UGLY CRAP with error lists
            if (parseParam.first is ErrorExpr) return getErrorPair(
                (parseParam.first as ErrorExpr).errors.first().message!!,
                parseParam.second
            ) else {
                params.add(parseParam.first as ParamExpr)
                index = parseParam.second + 1
            }
        }

        return Pair(
            ParameterListStmt(
                params
            ),
            index
        )
    }

    /**
     * IDENTIFIER ":" TYPE
     */
    private fun parseParamExpr(
        current: Int,
        tokens: List<DonkToken>
    ): Pair<BaseExpr, Int> {
        var index = current
        val id: DonkToken
        val type: TokenType

        if (matchTypes(tokens[index], TokenType.IDENTIFIER)) {
            id = tokens[index]
            index++
        } else return Pair(
            ErrorExpr(
                listOf(
                    ParserException("Expected IDENTIFIER at $index but got ${tokens[index].type}")
                )
            ),
            index
        )

        if (matchTypes(tokens[index], TokenType.COLON)) {
            index++
        } else return Pair(
            ErrorExpr(
                listOf(
                    ParserException("Expected COLON at $index but got ${tokens[index].type}")
                )
            ),
            index
        )

        if (matchTypes(
                tokens[index],
                *arrayOf(TokenType.TYPE_STRING, TokenType.TYPE_DOUBLE, TokenType.TYPE_BOOLEAN)
            )
        ) {
            type = tokens[index].type
            return Pair(
                ParamExpr(
                    id,
                    type
                ),
                index
            )
        } else return Pair(
            ErrorExpr(
                listOf(
                    ParserException("Expected COLON at $index but got ${tokens[index].type}")
                )
            ),
            index
        )


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
     * Parses a series of statements until the right brace is found
     */
    private fun parseBlock(current: Int, tokens: List<DonkToken>): Pair<BaseStmt, Int> {
        val statements = mutableListOf<BaseStmt>()
        var loop = true
        var index = current

        val matchBraceL = matchTypes(tokens[index], TokenType.LEFT_BRACE)

        if (matchBraceL) index++
        else return getErrorPair(
            "Expected LEFT_BRACE at $current, instead got ${tokens[current].type}",
            index
        )

        while (
            loop &&
            (index <= tokens.lastIndex)
        ) {
            if (matchTypes(tokens[index], TokenType.RIGHT_BRACE)) return Pair(
                BlockStmt(
                    statements
                ),
                index
            )

            val parseResult = parseDeclaration(
                index,
                tokens
            )

            when (parseResult.first) {
                is ErrorStmt -> return Pair(
                    parseResult.first,
                    parseResult.second
                )

               else -> {
                   statements.add(
                       parseResult.first
                   )

                   index = parseResult.second + 1
               }
            }
        }

        return Pair(
            BlockStmt(
                statements
            ),
            index
        )
    }



    /**
     * RETURN Expression ";"
     *
     * assume RETURN has been matched
     */
    private fun parseReturnStmt(current: Int, tokens: List<DonkToken>) : Pair<BaseStmt, Int> {
        var index = current
        val expr: BaseExpr

        val parseExpr = parseExpression(index, tokens)

        if (parseExpr.first is ErrorStmt) return Pair(
            parseExpr.first,
            parseExpr.second + 1
        ) else {
            expr = (parseExpr.first as ExprStmt).expr
            index = parseExpr.second + 1
        }

        if (matchTypes(tokens[index], TokenType.SEMICOLON)) return Pair(
            ReturnStmt(expr),
            index
        ) else return getErrorPair(
            "Expected SEMICOLON at $index but instead got ${tokens[index].type}",
            index
        )
    }

    fun parseWhileStmt(current: Int, tokens: List<DonkToken>) : Pair<BaseStmt, Int> {
        var index = current
        val conditionExpr: BaseExpr
        val block: BlockStmt

        if (matchTypes(tokens[index], TokenType.LEFT_PAREN)) index++
        else getErrorPair(
        "Expected LEFT_PAREN at $index but got ${tokens[index].type}",
        index
        )

        val parseConditionExpr = parseExpression(index, tokens)

        if (parseConditionExpr.first is ErrorStmt) return Pair(
            parseConditionExpr.first,
            parseConditionExpr.second
        ) else {
            conditionExpr = (parseConditionExpr.first as ExprStmt).expr
            index = parseConditionExpr.second + 1
        }

        if (matchTypes(tokens[index], TokenType.RIGHT_PAREN)) index++
        else getErrorPair(
            "Expected RIGHT_PAREN at $index but got ${tokens[index].type}",
            index
        )

        val parseBlock = parseBlock(index, tokens)

        if (parseBlock.first is ErrorStmt) return Pair(
            parseBlock.first,
            parseBlock.second
        ) else {
            block = (parseBlock.first as BlockStmt)
            index = parseBlock.second
        }

        return Pair(
            WhileStmt(
                conditionExpr,
                block
            ),
            index
        )
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
        var index = current
        //expect expr
        when (tokens[index].type) {
            TokenType.LITERAL_STRING,
            TokenType.LITERAL_NUMBER -> {
                expr = ExprStmt(LiteralExpr(tokens[current]))
            }
            TokenType.TRUE, TokenType.FALSE -> {
                expr = ExprStmt(BooleanExpr(tokens[current]))
            }
            else -> expr = ErrorStmt(
                listOf(
                    ParserException(
                        "Expected Expr but recieved ${tokens[index].type}"
                    )
                )
            )
        }

        return Pair(
            expr,
            index
        )
    }
}