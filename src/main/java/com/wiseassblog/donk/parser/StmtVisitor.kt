package com.wiseassblog.donk.parser

interface StmtVisitor<T> {
    fun visitExprStmt(stmt: ExprStmt): T
    fun visitBlockStmt(stmt: BlockStmt): T
    fun visitVarStmt(stmt: VarStmt): T
    fun visitValStmt(stmt: ValStmt): T
    fun visitIfStmt(stmt: IfStmt): T
    fun visitWhileStmt(stmt: WhileStmt): T
    fun visitFunctionStmt(stmt: FunctionStmt): T
    fun visitReturnStmt(stmt: ReturnStmt): T
    fun visitVoidStmt(stmt: VoidStmt): T
    fun visitErrorStmt(stmt: ErrorStmt): T
}