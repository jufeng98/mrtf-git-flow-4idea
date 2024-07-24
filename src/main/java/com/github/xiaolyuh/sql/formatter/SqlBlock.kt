package com.github.xiaolyuh.sql.formatter

import com.dbn.code.common.style.presets.CodeStylePreset
import com.github.xiaolyuh.sql.codestyle.SqlCodeStyleSettings
import com.github.xiaolyuh.sql.psi.*
import com.intellij.application.options.CodeStyle
import com.intellij.formatting.*
import com.intellij.lang.ASTNode
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiWhiteSpace
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.elementType


class SqlBlock(
    private val customSettings: SqlCodeStyleSettings,
    val psiElement: PsiElement,
    val parentBlock: SqlBlock?,
) : ASTBlock {
    private val childBlocks: MutableList<Block> by lazy {
        initChildBlocks()
    }

    private fun initChildBlocks(): MutableList<Block> {
        val list = mutableListOf<Block>()
        var child = psiElement.firstChild
        while (child != null) {
            if (child !is PsiWhiteSpace && child.textLength > 0) {
                val childBlock = SqlBlock(customSettings, child, this)
                list.add(childBlock)
            }
            child = child.nextSibling
        }
        return list
    }

    override fun getTextRange(): TextRange {
        return psiElement.textRange
    }

    override fun getSubBlocks(): MutableList<Block> {
        return childBlocks
    }

    override fun getWrap(): Wrap? {
        return CodeStylePreset.WRAP_NONE
    }

    override fun getIndent(): Indent? {
        if (psiElement is SqlBinaryAndExpr) {
            val sqlBinaryAndExpr = PsiTreeUtil.getParentOfType(psiElement, SqlBinaryAndExpr::class.java)
            if (sqlBinaryAndExpr != null) {
                return Indent.getLabelIndent()
            }
        }

        if (psiElement is SqlResultColumn
            || psiElement is SqlTableOrSubquery
            || psiElement is SqlBinaryAndExpr
            || psiElement is SqlOrderingTerm
        ) {
            val indentOptions = CodeStyle.getIndentOptions(psiElement.containingFile)
            return Indent.getSpaceIndent(indentOptions.INDENT_SIZE)
        }

        if (psiElement is SqlJoinOperator) {
            return Indent.getSpaceIndent(0)
        }

        return Indent.getLabelIndent()
    }

    override fun getAlignment(): Alignment? {
        return Alignment.createAlignment()
    }

    override fun getSpacing(child1: Block?, child2: Block): Spacing? {
        val leftBlock = child1 as SqlBlock?
        val leftPsiElement: PsiElement? = leftBlock?.psiElement

        val rightBlock = child2 as SqlBlock
        val rightPsiElement: PsiElement = rightBlock.psiElement

        if (rightPsiElement.elementType == SqlTypes.SELECT) {
            return Spacing.createSpacing(0, 0, 0, false, 0)
        }

        if (leftPsiElement == null) {
            return null
        }

        if (leftPsiElement.elementType == SqlTypes.COMMENT || rightPsiElement.elementType == SqlTypes.COMMENT) {
            return null
        }

        var spacing = lineFeed(leftPsiElement, rightPsiElement)
        if (spacing != null) {
            return spacing
        }

        spacing = trimSpace(leftPsiElement, rightPsiElement)
        if (spacing != null) {
            return spacing
        }

        spacing = oneSpace(leftPsiElement, rightPsiElement)
        if (spacing != null) {
            return spacing
        }

        return Spacing.createSpacing(0, 2, 0, false, 0)
    }

    private fun oneSpace(leftPsiElement: PsiElement, rightPsiElement: PsiElement): Spacing? {
        val need = leftPsiElement.elementType == SqlTypes.COMMA && rightPsiElement is SqlOrderingTerm
                || leftPsiElement is SqlColumnExpr && rightPsiElement.elementType == SqlTypes.EQ
                || leftPsiElement.elementType == SqlTypes.EQ && rightPsiElement is SqlColumnExpr

        if (need) {
            return Spacing.createSpacing(1, 1, 0, false, 0)
        }

        return null
    }

    private fun trimSpace(leftPsiElement: PsiElement, rightPsiElement: PsiElement): Spacing? {
        val need = leftPsiElement is SqlTableName && rightPsiElement.elementType == SqlTypes.DOT
                || leftPsiElement.elementType == SqlTypes.DOT && rightPsiElement is SqlColumnName
                || leftPsiElement is SqlOrderingTerm && rightPsiElement.elementType == SqlTypes.COMMA

        if (need) {
            return Spacing.createSpacing(0, 0, 0, false, 0)
        }

        return null
    }

    private fun lineFeed(leftPsiElement: PsiElement, rightPsiElement: PsiElement): Spacing? {
        val need = leftPsiElement.elementType == SqlTypes.SELECT && rightPsiElement is SqlResultColumn
                || leftPsiElement is SqlResultColumn && rightPsiElement.elementType == SqlTypes.FROM
                || leftPsiElement.elementType == SqlTypes.FROM
                || leftPsiElement.elementType == SqlTypes.COMMA && rightPsiElement is SqlResultColumn
                || leftPsiElement is SqlJoinClause
                || leftPsiElement is SqlJoinOperator
                || rightPsiElement is SqlJoinOperator
                || leftPsiElement.elementType == SqlTypes.WHERE
                || rightPsiElement.elementType == SqlTypes.ORDER
                || leftPsiElement.elementType == SqlTypes.BY && rightPsiElement is SqlOrderingTerm
                || rightPsiElement.elementType == SqlTypes.AND
                || leftPsiElement is SqlRoot

        if (need) {
            return Spacing.createSpacing(0, 0, 1, false, 0)
        }

        return null
    }

    override fun getChildAttributes(newChildIndex: Int): ChildAttributes {
        val subBlocks: List<Block> = subBlocks
        if (newChildIndex > subBlocks.size - 1) {
            return ChildAttributes(Indent.getNoneIndent(), Alignment.createAlignment())
        } else {
            val child = getSubBlocks()[newChildIndex]
            return ChildAttributes(child.indent, child.alignment)
        }
    }

    override fun isIncomplete(): Boolean {
        return false
    }

    override fun isLeaf(): Boolean {
        return psiElement.node.firstChildNode == null
    }

    override fun getNode(): ASTNode? {
        return psiElement.node
    }

    override fun toString(): String {
        return "psiElement:$psiElement"
    }

}
