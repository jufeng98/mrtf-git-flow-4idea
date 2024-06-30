// This is a generated file. Not intended for manual editing.
package com.github.xiaolyuh.sql.psi;

import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElementVisitor;
import com.alecstrong.sql.psi.core.psi.SqlCompositeElement;
import com.alecstrong.sql.psi.core.psi.TableElement;
import com.alecstrong.sql.psi.core.psi.NamedElement;
import com.alecstrong.sql.psi.core.psi.SqlBinaryExpr;
import com.alecstrong.sql.psi.core.psi.QueryElement;
import com.alecstrong.sql.psi.core.psi.AliasElement;

public class SqlVisitor extends PsiElementVisitor {

  public void visitAlterTableStmt(@NotNull SqlAlterTableStmt o) {
    visitCompositeElement(o);
  }

  public void visitAnalyzeStmt(@NotNull SqlAnalyzeStmt o) {
    visitCompositeElement(o);
  }

  public void visitAttachStmt(@NotNull SqlAttachStmt o) {
    visitCompositeElement(o);
  }

  public void visitBeginStmt(@NotNull SqlBeginStmt o) {
    visitCompositeElement(o);
  }

  public void visitBetweenExpr(@NotNull SqlBetweenExpr o) {
    visitExpr(o);
  }

  public void visitBinaryAddExpr(@NotNull SqlBinaryAddExpr o) {
    visitExpr(o);
    // visitBinaryExpr(o);
  }

  public void visitBinaryAndExpr(@NotNull SqlBinaryAndExpr o) {
    visitExpr(o);
    // visitBinaryExpr(o);
  }

  public void visitBinaryBitwiseExpr(@NotNull SqlBinaryBitwiseExpr o) {
    visitExpr(o);
    // visitBinaryExpr(o);
  }

  public void visitBinaryBooleanExpr(@NotNull SqlBinaryBooleanExpr o) {
    visitExpr(o);
    // visitBinaryExpr(o);
  }

  public void visitBinaryEqualityExpr(@NotNull SqlBinaryEqualityExpr o) {
    visitExpr(o);
    // visitBinaryExpr(o);
  }

  public void visitBinaryLikeExpr(@NotNull SqlBinaryLikeExpr o) {
    visitExpr(o);
  }

  public void visitBinaryLikeOperator(@NotNull SqlBinaryLikeOperator o) {
    visitCompositeElement(o);
  }

  public void visitBinaryMultExpr(@NotNull SqlBinaryMultExpr o) {
    visitExpr(o);
    // visitBinaryExpr(o);
  }

  public void visitBinaryOrExpr(@NotNull SqlBinaryOrExpr o) {
    visitExpr(o);
    // visitBinaryExpr(o);
  }

  public void visitBinaryPipeExpr(@NotNull SqlBinaryPipeExpr o) {
    visitExpr(o);
    // visitBinaryExpr(o);
  }

  public void visitBindExpr(@NotNull SqlBindExpr o) {
    visitExpr(o);
  }

  public void visitBindParameter(@NotNull SqlBindParameter o) {
    visitCompositeElement(o);
  }

  public void visitBlobLiteral(@NotNull SqlBlobLiteral o) {
    visitCompositeElement(o);
  }

  public void visitCaseExpr(@NotNull SqlCaseExpr o) {
    visitExpr(o);
  }

  public void visitCastExpr(@NotNull SqlCastExpr o) {
    visitExpr(o);
  }

  public void visitCollateExpr(@NotNull SqlCollateExpr o) {
    visitExpr(o);
  }

  public void visitCollationName(@NotNull SqlCollationName o) {
    visitCompositeElement(o);
  }

  public void visitColumnAlias(@NotNull SqlColumnAlias o) {
    visitAliasElement(o);
    // visitNamedElement(o);
  }

  public void visitColumnConstraint(@NotNull SqlColumnConstraint o) {
    visitCompositeElement(o);
  }

  public void visitColumnDef(@NotNull SqlColumnDef o) {
    visitCompositeElement(o);
  }

  public void visitColumnExpr(@NotNull SqlColumnExpr o) {
    visitExpr(o);
  }

  public void visitColumnName(@NotNull SqlColumnName o) {
    visitNamedElement(o);
  }

  public void visitCommitStmt(@NotNull SqlCommitStmt o) {
    visitCompositeElement(o);
  }

  public void visitCompoundOperator(@NotNull SqlCompoundOperator o) {
    visitCompositeElement(o);
  }

  public void visitCompoundSelectStmt(@NotNull SqlCompoundSelectStmt o) {
    visitQueryElement(o);
  }

  public void visitConflictClause(@NotNull SqlConflictClause o) {
    visitCompositeElement(o);
  }

  public void visitCreateIndexStmt(@NotNull SqlCreateIndexStmt o) {
    visitCompositeElement(o);
  }

  public void visitCreateTableStmt(@NotNull SqlCreateTableStmt o) {
    visitTableElement(o);
  }

  public void visitCreateTriggerStmt(@NotNull SqlCreateTriggerStmt o) {
    visitCompositeElement(o);
  }

  public void visitCreateViewStmt(@NotNull SqlCreateViewStmt o) {
    visitTableElement(o);
  }

  public void visitCreateVirtualTableStmt(@NotNull SqlCreateVirtualTableStmt o) {
    visitTableElement(o);
  }

  public void visitCteTableName(@NotNull SqlCteTableName o) {
    visitCompositeElement(o);
  }

  public void visitDatabaseName(@NotNull SqlDatabaseName o) {
    visitCompositeElement(o);
  }

  public void visitDeleteStmt(@NotNull SqlDeleteStmt o) {
    visitCompositeElement(o);
  }

  public void visitDeleteStmtLimited(@NotNull SqlDeleteStmtLimited o) {
    visitCompositeElement(o);
  }

  public void visitDetachStmt(@NotNull SqlDetachStmt o) {
    visitCompositeElement(o);
  }

  public void visitDropIndexStmt(@NotNull SqlDropIndexStmt o) {
    visitCompositeElement(o);
  }

  public void visitDropTableStmt(@NotNull SqlDropTableStmt o) {
    visitCompositeElement(o);
  }

  public void visitDropTriggerStmt(@NotNull SqlDropTriggerStmt o) {
    visitCompositeElement(o);
  }

  public void visitDropViewStmt(@NotNull SqlDropViewStmt o) {
    visitCompositeElement(o);
  }

  public void visitErrorMessage(@NotNull SqlErrorMessage o) {
    visitCompositeElement(o);
  }

  public void visitExistsExpr(@NotNull SqlExistsExpr o) {
    visitExpr(o);
  }

  public void visitExpr(@NotNull SqlExpr o) {
    visitCompositeElement(o);
  }

  public void visitForeignKeyClause(@NotNull SqlForeignKeyClause o) {
    visitCompositeElement(o);
  }

  public void visitForeignTable(@NotNull SqlForeignTable o) {
    visitNamedElement(o);
  }

  public void visitFunctionExpr(@NotNull SqlFunctionExpr o) {
    visitExpr(o);
  }

  public void visitFunctionName(@NotNull SqlFunctionName o) {
    visitCompositeElement(o);
  }

  public void visitIdentifier(@NotNull SqlIdentifier o) {
    visitCompositeElement(o);
  }

  public void visitInExpr(@NotNull SqlInExpr o) {
    visitExpr(o);
  }

  public void visitIndexName(@NotNull SqlIndexName o) {
    visitCompositeElement(o);
  }

  public void visitIndexedColumn(@NotNull SqlIndexedColumn o) {
    visitCompositeElement(o);
  }

  public void visitInsertStmt(@NotNull SqlInsertStmt o) {
    visitCompositeElement(o);
  }

  public void visitIsExpr(@NotNull SqlIsExpr o) {
    visitExpr(o);
  }

  public void visitJoinClause(@NotNull SqlJoinClause o) {
    visitQueryElement(o);
  }

  public void visitJoinConstraint(@NotNull SqlJoinConstraint o) {
    visitCompositeElement(o);
  }

  public void visitJoinOperator(@NotNull SqlJoinOperator o) {
    visitCompositeElement(o);
  }

  public void visitLimitingTerm(@NotNull SqlLimitingTerm o) {
    visitCompositeElement(o);
  }

  public void visitLiteralExpr(@NotNull SqlLiteralExpr o) {
    visitExpr(o);
  }

  public void visitLiteralValue(@NotNull SqlLiteralValue o) {
    visitCompositeElement(o);
  }

  public void visitModuleArgument(@NotNull SqlModuleArgument o) {
    visitCompositeElement(o);
  }

  public void visitModuleName(@NotNull SqlModuleName o) {
    visitCompositeElement(o);
  }

  public void visitNewTableName(@NotNull SqlNewTableName o) {
    visitCompositeElement(o);
  }

  public void visitNullExpr(@NotNull SqlNullExpr o) {
    visitExpr(o);
  }

  public void visitNumericLiteral(@NotNull SqlNumericLiteral o) {
    visitCompositeElement(o);
  }

  public void visitOrderingTerm(@NotNull SqlOrderingTerm o) {
    visitCompositeElement(o);
  }

  public void visitParenExpr(@NotNull SqlParenExpr o) {
    visitExpr(o);
  }

  public void visitPragmaName(@NotNull SqlPragmaName o) {
    visitCompositeElement(o);
  }

  public void visitPragmaStmt(@NotNull SqlPragmaStmt o) {
    visitCompositeElement(o);
  }

  public void visitPragmaValue(@NotNull SqlPragmaValue o) {
    visitCompositeElement(o);
  }

  public void visitQualifiedTableName(@NotNull SqlQualifiedTableName o) {
    visitCompositeElement(o);
  }

  public void visitRaiseExpr(@NotNull SqlRaiseExpr o) {
    visitExpr(o);
  }

  public void visitRaiseFunction(@NotNull SqlRaiseFunction o) {
    visitCompositeElement(o);
  }

  public void visitReindexStmt(@NotNull SqlReindexStmt o) {
    visitCompositeElement(o);
  }

  public void visitReleaseStmt(@NotNull SqlReleaseStmt o) {
    visitCompositeElement(o);
  }

  public void visitResultColumn(@NotNull SqlResultColumn o) {
    visitQueryElement(o);
  }

  public void visitRollbackStmt(@NotNull SqlRollbackStmt o) {
    visitCompositeElement(o);
  }

  public void visitSavepointName(@NotNull SqlSavepointName o) {
    visitCompositeElement(o);
  }

  public void visitSavepointStmt(@NotNull SqlSavepointStmt o) {
    visitCompositeElement(o);
  }

  public void visitSelectStmt(@NotNull SqlSelectStmt o) {
    visitQueryElement(o);
  }

  public void visitSetterExpression(@NotNull SqlSetterExpression o) {
    visitCompositeElement(o);
  }

  public void visitSignedNumber(@NotNull SqlSignedNumber o) {
    visitCompositeElement(o);
  }

  public void visitSqlStmt(@NotNull SqlSqlStmt o) {
    visitCompositeElement(o);
  }

  public void visitStatement(@NotNull SqlStatement o) {
    visitCompositeElement(o);
  }

  public void visitStmtList(@NotNull SqlStmtList o) {
    visitCompositeElement(o);
  }

  public void visitStringLiteral(@NotNull SqlStringLiteral o) {
    visitCompositeElement(o);
  }

  public void visitTableAlias(@NotNull SqlTableAlias o) {
    visitNamedElement(o);
    // visitAliasElement(o);
  }

  public void visitTableConstraint(@NotNull SqlTableConstraint o) {
    visitCompositeElement(o);
  }

  public void visitTableName(@NotNull SqlTableName o) {
    visitNamedElement(o);
  }

  public void visitTableOrIndexName(@NotNull SqlTableOrIndexName o) {
    visitCompositeElement(o);
  }

  public void visitTableOrSubquery(@NotNull SqlTableOrSubquery o) {
    visitQueryElement(o);
  }

  public void visitTriggerName(@NotNull SqlTriggerName o) {
    visitCompositeElement(o);
  }

  public void visitTypeName(@NotNull SqlTypeName o) {
    visitCompositeElement(o);
  }

  public void visitUnaryExpr(@NotNull SqlUnaryExpr o) {
    visitExpr(o);
  }

  public void visitUpdateStmt(@NotNull SqlUpdateStmt o) {
    visitCompositeElement(o);
  }

  public void visitUpdateStmtLimited(@NotNull SqlUpdateStmtLimited o) {
    visitCompositeElement(o);
  }

  public void visitUpdateStmtSubsequentSetter(@NotNull SqlUpdateStmtSubsequentSetter o) {
    visitCompositeElement(o);
  }

  public void visitVacuumStmt(@NotNull SqlVacuumStmt o) {
    visitCompositeElement(o);
  }

  public void visitValuesExpression(@NotNull SqlValuesExpression o) {
    visitCompositeElement(o);
  }

  public void visitViewName(@NotNull SqlViewName o) {
    visitNamedElement(o);
  }

  public void visitWithClause(@NotNull SqlWithClause o) {
    visitCompositeElement(o);
  }

  public void visitAliasElement(@NotNull AliasElement o) {
    visitElement(o);
  }

  public void visitNamedElement(@NotNull NamedElement o) {
    visitElement(o);
  }

  public void visitQueryElement(@NotNull QueryElement o) {
    visitElement(o);
  }

  public void visitTableElement(@NotNull TableElement o) {
    visitElement(o);
  }

  public void visitCompositeElement(@NotNull SqlCompositeElement o) {
    visitElement(o);
  }

}
