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
 * Note:
 * - current is treated as an immutable (in theory) representation of the "current" position of the Parser within
 * the list of tokens as the Function begins executing.
 * - index is a variable which will start at current, and then move based on the state machine's (the parsing and
 * matching functions) execution.
 *
 *
 * parseStatement, and parseDeclaration all increment index by 1 larger than the parsed statement.
 * However, other functions increment the index to the last token of the parsed statement. This is necessary to
 * establish consistency and flexibility over the index; alongside extensive testing of course.
 *
 *
 *
 */
class DonkHandWrittenDonkParser() : IParser {
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

            TokenType.IF -> {
                index++
                val parseIf = parseIfStmt(index, tokens)

                return Pair(
                    parseIf.first,
                    parseIf.second + 1
                )
            }
        }

        val parseExpressionResult = parseExpression(current, tokens)
        return Pair<BaseStmt, Int>(parseExpressionResult.first, current + parseExpressionResult.second + 1)
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
            TokenType.LITERAL_NUMBER,
            TokenType.IDENTIFIER -> {
                val parseLit = parseLiteral(index, tokens)
                return Pair(
                    ExprStmt(parseLit.first),
                    parseLit.second
                )
            }
            TokenType.TRUE, TokenType.FALSE -> {
                expr = ExprStmt(BooleanExpr(tokens[index]))
                index++
            }
            TokenType.LEFT_PAREN -> {
                val parseGroup = parseGroupingExpression(index + 1, tokens)

                return Pair(
                    ExprStmt(parseGroup.first),
                    parseGroup.second
                )
            }

            TokenType.EXCLM -> {
                val parseUn = parseUnaryExpression(index, tokens)

                return Pair(
                    parseUn.first,
                    parseUn.second
                )
            }

            else -> {
                return Pair(
                    ErrorStmt(
                        listOf(
                            ParserException(
                                "Expected Expr at $index but recieved ${tokens[index].type}"
                            )
                        )
                    ),
                    index
                )
            }
        }

        return Pair(
            expr,
            index
        )
    }

    private fun parseLiteral(current: Int, tokens: List<DonkToken>): Pair<BaseExpr, Int> {
        var index = current
        val curTok = tokens[index]

        if (index >= tokens.size - 1) return Pair(LiteralExpr(tokens[index]), index + 1)

        val next = tokens[index + 1]

        return when {
            matchTypes(next, *arrayOf(TokenType.COMMA, TokenType.SEMICOLON, TokenType.RIGHT_PAREN))
            -> Pair(LiteralExpr(tokens[index]), index + 1)

            matchTypes(next, *arrayOf(TokenType.PLUS, TokenType.MINUS, TokenType.ASTERISK, TokenType.SLASH))
            -> parseBinaryExpr(LiteralExpr(curTok), index + 1, tokens)

            matchTypes(
                next, *arrayOf(
                    TokenType.EQUAL_EQUAL, TokenType.LESS, TokenType.LESS_EQUAL, TokenType.GREATER,
                    TokenType.GREATER_EQUAL, TokenType.EXCLM_EQUAL
                )
            )
            -> parseLogicalExpr(LiteralExpr(curTok), index + 1, tokens)

            //may only assign identifiers, also starts after equal
            matchTypes(next, *arrayOf(TokenType.EQUAL)) && curTok.type == TokenType.IDENTIFIER
            -> parseAssExpr(curTok, index + 2, tokens)

            matchTypes(next, TokenType.LEFT_PAREN)
            -> {
                //may be a binary/logical after
                val parseCall = parseCallExpr(index, tokens)
                if (parseCall.second < tokens.size - 1 &&
                    matchTypes(
                        tokens[parseCall.second + 1], *arrayOf(
                            TokenType.PLUS, TokenType.MINUS, TokenType.ASTERISK, TokenType.SLASH,
                            TokenType.EQUAL_EQUAL, TokenType.LESS, TokenType.LESS_EQUAL, TokenType.GREATER,
                            TokenType.GREATER_EQUAL, TokenType.EXCLM_EQUAL
                        )
                    )
                ) {
                    return parseBinaryExpr(parseCall.first, parseCall.second + 1, tokens)
                } else {
                    return Pair(
                        parseCall.first,
                        parseCall.second
                    )
                }
            }

            else -> {
                Pair(
                    ErrorExpr(
                        listOf(
                            ParserException(
                                "Expected Expr at $index but recieved ${tokens[index].type}"
                            )
                        )
                    ),
                    index
                )
            }


        }
    }

    /**
     * Assume we already know this is a Logical Operator
     * Starts at operator
     */
    private fun parseLogicalExpr(left: BaseExpr, current: Int, tokens: List<DonkToken>): Pair<BaseExpr, Int> {
        var index = current
        val operator: DonkToken
        val right: BaseExpr

        operator = tokens[index]

        //parse right side
        val parseRight = parseExpression(
            index + 1,
            tokens
        )

        if (parseRight.first is ErrorStmt) return Pair(
            ErrorExpr(
                listOf(
                    ParserException(
                        (parseRight.first as ErrorStmt).errors.first().message!!
                    )
                )
            ),
            parseRight.second
        ) else {
            right = (parseRight.first as ExprStmt).expr
            index = parseRight.second
        }

        return Pair(
            LogicalExpr(
                left,
                operator,
                right
            ),
            index
        )
    }

    /**
     * Starts at Operator
     *
     * Expression OPERATOR Expression
     *
     */
    private fun parseBinaryExpr(left: BaseExpr, current: Int, tokens: List<DonkToken>): Pair<BaseExpr, Int> {
        var index = current
        val operator: DonkToken
        val prec: Precedence
        val right: BaseExpr

        operator = tokens[index]

        when (operator.type) {
            TokenType.PLUS, TokenType.MINUS -> {
                prec = Precedence.LOW
            }
            else -> {
                prec = Precedence.MEDIUM
            }
        }

        index++

        //parse right side
        val parseRight = parseExpression(
            index,
            tokens
        )

        if (parseRight.first is ErrorStmt) return Pair(
            ErrorExpr(
                listOf(
                    ParserException(
                        (parseRight.first as ErrorStmt).errors.first().message!!
                    )
                )
            ),
            parseRight.second
        ) else {
            right = (parseRight.first as ExprStmt).expr
            index = parseRight.second
        }

        return Pair(
            BinaryExpr(
                left,
                operator,
                right,
                prec
            ),
            index
        )
    }

    /**
     * Starts after Identifier and assignment, also assume it is an identifier
     */
    private fun parseAssExpr(identifier: DonkToken, current: Int, tokens: List<DonkToken>): Pair<BaseExpr, Int> {
        var index = current

        val parseExpr = parseExpression(index, tokens)

        return if (parseExpr.first is ErrorStmt) return Pair(
            ErrorExpr(
                listOf(
                    (parseExpr.first as ErrorStmt).errors.first()
                )
            ),
            parseExpr.second
        ) else {
            Pair(
                AssignExpr(
                    identifier,
                    (parseExpr.first as ExprStmt).expr
                ),
                parseExpr.second
            )
        }
    }

    /**
     * Assumes current starts at the ID, but we already know that the next token was "("
     *
     * Params can either be Literals or other call expressions
     */
    private fun parseCallExpr(current: Int, tokens: List<DonkToken>): Pair<BaseExpr, Int> {
        var index = current
        val args = mutableListOf<BaseExpr>()

        val functionId = tokens[index]

        //move past both the ID and the right paren
        index += 2

        if (matchTypes(tokens[index], TokenType.RIGHT_PAREN)) return Pair(
            CallExpr(
                functionId,
                emptyList()
            ),
            index
        )

        while (tokens[index].type != TokenType.RIGHT_PAREN) {
            if (index >= tokens.size) return Pair(
                ErrorExpr(
                    listOf(ParserException("Expected Right Paren but reached EOF"))
                ),
                index

            )
            if (matchTypes(tokens[index], TokenType.COMMA)) {
                index++
                continue
            }

            if (matchTypes(
                    tokens[index],
                    *arrayOf(TokenType.LITERAL_STRING, TokenType.LITERAL_NUMBER, TokenType.IDENTIFIER)
                )
            ) {
                val parseArgs = parseExpression(index, tokens)

                if (parseArgs.first is ErrorStmt) return Pair(
                    ErrorExpr(
                        listOf(
                            (parseArgs.first as ErrorStmt).errors.first()
                        )
                    ),
                    parseArgs.second
                ) else {
                    args.add((parseArgs.first as ExprStmt).expr)
                    index = parseArgs.second
                }
            }
        }

        return Pair(
            CallExpr(
                functionId,
                args
            ),
            index + 1
        )
    }

    /**
     * Starts at Operator
     *
     * Expression OPERATOR Expression
     *
     */
    private fun parseUnaryExpression(current: Int, tokens: List<DonkToken>): Pair<BaseStmt, Int> {
        var index = current
        val operator: DonkToken
        val right: BaseExpr

        operator = tokens[index]

        //parse left side
        val parseRight = parseExpression(
            index + 1,
            tokens
        )

        if (parseRight.first is ErrorStmt) return Pair(
            parseRight.first,
            parseRight.second
        ) else {
            right = (parseRight.first as ExprStmt).expr
            index = parseRight.second
        }

        return Pair(
            ExprStmt(
                UnaryExpr(
                    operator, right
                )
            ),
            index
        )
    }

    /**
     * Starts after paren
     */
    private fun parseGroupingExpression(current: Int, tokens: List<DonkToken>): Pair<BaseExpr, Int> {
        var index = current

        val parseExpr = parseExpression(
            index,
            tokens
        )

        //move to expected paren
        index = parseExpr.second

        if (matchTypes(tokens[index], TokenType.RIGHT_PAREN)){
            if (parseExpr.first is ExprStmt) return Pair(
                GroupingExpr((parseExpr.first as ExprStmt).expr),
                //consume the paren
                index + 1
            ) else return Pair(
                ErrorExpr(
                    listOf(
                        ParserException("Expected ExprStmt at $index but got ${parseExpr.toString()}")
                    )
                ),
                index
            )

        } else return Pair(
            ErrorExpr(
                listOf(
                    ParserException("Expected RIGHT_PAREN at $index but got ${tokens[index].type}")
                )
            ),
            index
        )

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
    private fun parseReturnStmt(current: Int, tokens: List<DonkToken>): Pair<BaseStmt, Int> {
        var index = current
        val expr: BaseExpr

        val parseExpr = parseExpression(index, tokens)

        if (parseExpr.first is ErrorStmt) return Pair(
            parseExpr.first,
            parseExpr.second
        ) else {
            expr = (parseExpr.first as ExprStmt).expr
            index = parseExpr.second
        }

        if (matchTypes(tokens[index], TokenType.SEMICOLON)) return Pair(
            ReturnStmt(expr),
            index
        ) else return getErrorPair(
            "Expected SEMICOLON at $index but instead got ${tokens[index].type}",
            index
        )
    }

    fun parseWhileStmt(current: Int, tokens: List<DonkToken>): Pair<BaseStmt, Int> {
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

    fun parseIfStmt(current: Int, tokens: List<DonkToken>): Pair<BaseStmt, Int> {
        var index = current
        val conditionExpr: BaseExpr
        val ifBlock: BlockStmt
        val elseBlock: BlockStmt

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

        val parseIfBlock = parseBlock(index, tokens)

        if (parseIfBlock.first is ErrorStmt) return Pair(
            parseIfBlock.first,
            parseIfBlock.second
        ) else {
            ifBlock = (parseIfBlock.first as BlockStmt)
            index = parseIfBlock.second
        }

        if ((index + 1 < tokens.size) && matchTypes(tokens[index + 1], TokenType.ELSE)) {
            //eat last bracket of ifBlock and also the ELSE
            index += 2

            val parseElseBlock = parseBlock(index, tokens)

            if (parseElseBlock.first is ErrorStmt) return Pair(
                parseElseBlock.first,
                parseElseBlock.second
            ) else {
                elseBlock = (parseElseBlock.first as BlockStmt)
                index = parseElseBlock.second
            }

            return Pair(
                IfStmt(
                    conditionExpr,
                    ifBlock,
                    elseBlock
                ),
                index
            )
        } else {
            return Pair(
                IfStmt(
                    conditionExpr,
                    ifBlock
                ),
                index
            )
        }
    }
}

