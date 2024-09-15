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

/**
 * @author yudong
 */
class HttpFakePsiElement(httpUrl: HttpUrl, private val searchTxt: String) :
    ASTWrapperPsiElement(httpUrl.node) {

    override fun getPresentation(): ItemPresentation {
        return HttpItemPresentation
    }

    override fun navigate(requestFocus: Boolean) {
        val event = createEvent()
        val seManager = SearchEverywhereManager.getInstance(project)
        seManager.show(ApiAbstractGotoSEContributor::class.java.simpleName, searchTxt, event)
    }

    companion object {
        @Suppress("DEPRECATION")
        fun createEvent(): AnActionEvent {
            return AnActionEvent(
                null,
                DataManager.getInstance().dataContext,
                "",
                Presentation(""),
                ActionManager.getInstance(),
                1
            )
        }
    }
}
