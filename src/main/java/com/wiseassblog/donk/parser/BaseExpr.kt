package com.wiseassblog.donk.parser

import com.wiseassblog.donk.DonkToken

abstract class BaseExpr {
    abstract fun <T> accept(visitor: ExprVisitor<T>) : T
}

class ErrorExpr(
    val errors: List<ParserException>
): BaseExpr() {
    override fun <T> accept(visitor: ExprVisitor<T>): T =
        visitor.visitErrorExpr(this)
}

class VoidExpr() : BaseExpr() {
    override fun <T> accept(visitor: ExprVisitor<T>): T =
        visitor.visitVoidExpr(this)
}

data class AssignExpr(
    val identifier: DonkToken,
    val value: BaseExpr
) : BaseExpr() {
    override fun <T> accept(visitor: ExprVisitor<T>): T =
        visitor.visitAssignExpr(this)
}

data class LiteralExpr(
    val literal: DonkToken
) : BaseExpr() {
    override fun <T> accept(visitor: ExprVisitor<T>): T =
        visitor.visitLiteralExpr(this)
}

data class BinaryExpr(
    val left: BaseExpr,
    val operator: DonkToken,
    val right: BaseExpr
) : BaseExpr() {
    override fun <T> accept(visitor: ExprVisitor<T>): T =
        visitor.visitBinaryExpr(this)
}

data class UnaryExpr(
    val operator: DonkToken,
    val rightExpr: BaseExpr
) : BaseExpr() {
    override fun <T> accept(visitor: ExprVisitor<T>): T =
        visitor.visitUnaryExpr(this)
}

data class GroupingExpr(
    val expr: BaseExpr
) : BaseExpr() {
    override fun <T> accept(visitor: ExprVisitor<T>): T =
        visitor.visitGroupingExpr(this)
}

data class VariableExpr(
    val identifier: DonkToken
) : BaseExpr() {
    override fun <T> accept(visitor: ExprVisitor<T>): T =
        visitor.visitVariableExpr(this)
}

data class ValueExpr(
    val identifier: DonkToken
) : BaseExpr() {
    override fun <T> accept(visitor: ExprVisitor<T>): T =
        visitor.visitValueExpr(this)
}

data class LogicalExpr(
    val left: BaseExpr,
    val operator: DonkToken,
    val right: BaseExpr
) : BaseExpr() {
    override fun <T> accept(visitor: ExprVisitor<T>): T =
        visitor.visitLogicalExpr(this)
}

data class CallExpr(
    val function: BaseExpr,
    val parenthesis: DonkToken,
    val arguments: List<BaseExpr>
) : BaseExpr() {
    override fun <T> accept(visitor: ExprVisitor<T>): T =
        visitor.visitCallExpr(this)
}

data class FunctionExpr(
    val parameters: List<DonkToken>,
    val body: List<BaseStmt>
) : BaseExpr() {
    override fun <T> accept(visitor: ExprVisitor<T>): T =
        visitor.visitFunctionExpr(this)
}
