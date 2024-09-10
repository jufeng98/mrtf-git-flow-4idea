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
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReferenceBase
import javax.swing.Icon

class HttpPsiReference(httpUrl: HttpUrl, private val searchTxt: String, rangeInElement: TextRange) :
    PsiReferenceBase<HttpUrl>(httpUrl, rangeInElement) {

    override fun resolve(): PsiElement {
        return HttpFakePsiElement(myElement, searchTxt)
    }

    class HttpFakePsiElement(httpUrl: HttpUrl, private val searchTxt: String) :
        ASTWrapperPsiElement(httpUrl.node) {

        override fun getName(): String {
            return "搜索对应的Controller接口"
        }

        override fun getPresentation(): ItemPresentation {
            return object : ItemPresentation {
                override fun getPresentableText(): String {
                    return name
                }

                override fun getIcon(unused: Boolean): Icon? {
                    return null
                }

            }
        }

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

}
