package com.github.xiaolyuh.http.runconfig

import com.github.xiaolyuh.http.gutter.HttpGutterIconNavigationHandler
import com.github.xiaolyuh.http.psi.HttpMethod
import com.github.xiaolyuh.utils.HttpUtils
import com.intellij.execution.ExecutionResult
import com.intellij.execution.Executor
import com.intellij.execution.configurations.RunProfileState
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.execution.runners.ProgramRunner
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.PsiUtil
import java.io.File

class HttpRunProfileState(
    val project: Project,
    private val environment: ExecutionEnvironment,
    private val env: String,
    private val httpFilePath: String,
) : RunProfileState {
    override fun execute(executor: Executor?, runner: ProgramRunner<*>): ExecutionResult? {
        val virtualFile = VfsUtil.findFileByIoFile(File(httpFilePath), true) ?: return null

        val psiFile = PsiUtil.getPsiFile(project, virtualFile)
        val httpMethods = PsiTreeUtil.findChildrenOfType(psiFile, HttpMethod::class.java)

        val httpMethod = httpMethods.firstOrNull {
            val tabName = HttpUtils.getTabName(it) ?: ""
            environment.runProfile.name == tabName
        }
        if (httpMethod == null) {
            return null
        }

        val handler = HttpGutterIconNavigationHandler(httpMethod)
        handler.doRequest(null, env)

        return null
    }

}
