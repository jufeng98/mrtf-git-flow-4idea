package com.github.xiaolyuh.http.reference

import com.cool.request.view.tool.search.ApiAbstractGotoSEContributor
import com.github.xiaolyuh.http.psi.HttpUrl
import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.ide.DataManager
import com.intellij.ide.actions.searcheverywhere.SearchEverywhereManager
import com.intellij.navigation.ItemPresentation
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.Presentation

class HttpFakePsiElement(httpUrl: HttpUrl, private val searchTxt: String) :
    ASTWrapperPsiElement(httpUrl.node) {


    override fun getPresentation(): ItemPresentation {
        return HttpItemPresentation
    }

    @Suppress("DEPRECATION")
    override fun navigate(requestFocus: Boolean) {
        val event =
            AnActionEvent(
                null,
                DataManager.getInstance().dataContext,
                "",
                Presentation(""),
                ActionManager.getInstance(),
                1
            )
        val seManager = SearchEverywhereManager.getInstance(project)
        seManager.show(ApiAbstractGotoSEContributor::class.java.simpleName, searchTxt, event)
    }
}
