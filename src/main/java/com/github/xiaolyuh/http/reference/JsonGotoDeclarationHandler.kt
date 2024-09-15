package com.github.xiaolyuh.http.reference

import com.github.xiaolyuh.http.psi.HttpOrdinaryContent
import com.github.xiaolyuh.http.psi.HttpRequest
import com.github.xiaolyuh.http.psi.HttpUrl
import com.github.xiaolyuh.utils.HttpUtils
import com.intellij.codeInsight.navigation.actions.GotoDeclarationHandler
import com.intellij.json.psi.JsonStringLiteral
import com.intellij.lang.injection.InjectedLanguageManager
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil

/**
 * @author yudong
 */
class JsonGotoDeclarationHandler : GotoDeclarationHandler {

    override fun getGotoDeclarationTargets(
        element: PsiElement?,
        offset: Int,
        editor: Editor?,
    ): Array<PsiElement> {
        if (element == null) {
            return arrayOf()
        }

        val jsonString = element.parent
        if (jsonString !is JsonStringLiteral) {
            return arrayOf()
        }

        if (!jsonString.isPropertyName) {
            return arrayOf()
        }

        val injectionHost = InjectedLanguageManager.getInstance(jsonString.project).getInjectionHost(jsonString)
        if (injectionHost !is HttpOrdinaryContent) {
            return arrayOf()
        }

        val httpRequest = PsiTreeUtil.getParentOfType(injectionHost, HttpRequest::class.java)
        val httpUrl = PsiTreeUtil.getChildOfType(httpRequest, HttpUrl::class.java) ?: return arrayOf()
        val pair = HttpUtils.getSearchTxtInfo(httpUrl) ?: return arrayOf()

        return arrayOf(JsonFakePsiElement(jsonString, pair.first))
    }

}
