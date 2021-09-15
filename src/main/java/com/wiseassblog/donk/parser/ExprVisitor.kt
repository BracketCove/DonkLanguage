package com.wiseassblog.donk.parser

interface ExprVisitor<T> {
    fun visitBinaryExpr(expr: BinaryExpr): T
    fun visitGroupingExpr(expr: GroupingExpr): T
    fun visitLiteralExpr(expr: LiteralExpr): T
    fun visitUnaryExpr(expr: UnaryExpr): T
    fun visitVariableExpr(expr: VariableExpr): T
    fun visitValueExpr(expr: ValueExpr): T
    fun visitAssignExpr(expr: AssignExpr): T
    fun visitParamExpr(expr: ParamExpr): T
    fun visitLogicalExpr(expr: LogicalExpr): T
    fun visitCallExpr(expr: CallExpr): T
    fun visitFunctionExpr(expr: FunctionExpr): T
    fun visitVoidExpr(expr: VoidExpr): T
    fun visitErrorExpr(expr: ErrorExpr): T
}
