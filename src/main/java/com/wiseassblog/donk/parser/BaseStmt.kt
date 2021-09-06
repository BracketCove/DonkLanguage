package com.wiseassblog.donk.parser

import com.wiseassblog.donk.DonkToken

abstract class BaseStmt() {
    abstract fun <T> accept(visitor: StmtVisitor<T>) : T
}

class ErrorStmt(
    val errors: List<ParserException>
): BaseStmt() {
    override fun <T> accept(visitor: StmtVisitor<T>): T =
        visitor.visitErrorStmt(this)
}

class VoidStmt(): BaseStmt() {
    override fun <T> accept(visitor: StmtVisitor<T>): T =
        visitor.visitVoidStmt(this)
}

data class ExprStmt(
    val expr: BaseExpr
) : BaseStmt() {
    override fun <T> accept(visitor: StmtVisitor<T>): T =
        visitor.visitExprStmt(this)

}

data class BlockStmt(
    val statements: List<BaseStmt>
) : BaseStmt() {
    override fun <T> accept(visitor: StmtVisitor<T>): T =
        visitor.visitBlockStmt(this)
}

data class VarStmt(
    val identifier: DonkToken,
    val type: DonkToken,
    val initializer: BaseExpr
) : BaseStmt() {
    override fun <T> accept(visitor: StmtVisitor<T>): T =
        visitor.visitVarStmt(this)
}

data class ValStmt(
    val identifier: DonkToken,
    val type: DonkToken,
    val initializer: BaseExpr
) : BaseStmt() {
    override fun <T> accept(visitor: StmtVisitor<T>): T =
        visitor.visitValStmt(this)
}

data class IfStmt(
    val condition: BaseExpr,
    val ifTrue: BaseStmt,
    val ifFalse: BaseStmt = VoidStmt()
) : BaseStmt() {
    override fun <T> accept(visitor: StmtVisitor<T>): T =
        visitor.visitIfStmt(this)
}

data class WhileStmt(
    val condition: BaseExpr,
    val body: BaseStmt
) : BaseStmt() {
    override fun <T> accept(visitor: StmtVisitor<T>): T =
        visitor.visitWhileStmt(this)
}

data class FunctionStmt(
    val identifier: DonkToken,
    val body: FunctionExpr
) : BaseStmt() {
    override fun <T> accept(visitor: StmtVisitor<T>): T =
        visitor.visitFunctionStmt(this)
}

data class ReturnStmt(
    val keyword: DonkToken,
    val value: BaseExpr
) : BaseStmt() {
    override fun <T> accept(visitor: StmtVisitor<T>): T =
        visitor.visitReturnStmt(this)
}

