package com.github.xiaolyuh.http.reference

import com.github.xiaolyuh.http.env.EnvFileService
import com.github.xiaolyuh.http.reference.HttpFakePsiElement.Companion.showTip
import com.github.xiaolyuh.http.ui.HttpEditorTopForm
import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.psi.PsiElement

/**
 * @author yudong
 */
class HttpVariableFakePsiElement(private val element: PsiElement, private val variableName: String) :
    ASTWrapperPsiElement(element.node) {

    override fun navigate(requestFocus: Boolean) {
        val selectedEnv = HttpEditorTopForm.getSelectedEnv(project) ?: "dev"

        val path = element.containingFile.virtualFile.parent.path

        val jsonLiteral = EnvFileService.getEnvEle(variableName, selectedEnv, path, project)
        if (jsonLiteral == null) {
            showTip("在环境文件中未能解析该变量", project)
            return
        }

        jsonLiteral.navigate(true)
    }

}
