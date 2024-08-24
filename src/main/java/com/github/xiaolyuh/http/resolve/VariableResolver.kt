package com.github.xiaolyuh.http.resolve

import com.dbn.common.util.UUIDs
import com.github.xiaolyuh.http.js.JsScriptExecutor
import com.github.xiaolyuh.http.service.EnvFileService
import com.github.xiaolyuh.http.ui.HttpEditorTopForm
import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import org.apache.commons.lang.math.RandomUtils
import org.apache.commons.lang3.RandomStringUtils
import java.util.regex.Pattern

@Service(Service.Level.PROJECT)
class VariableResolver(private val project: Project) {
    private val pattern = Pattern.compile("(\\{\\{[a-zA-Z0-9.()\\-,\$]+}})", Pattern.MULTILINE)
    private val patternNotNumber = Pattern.compile("\\D")

    fun resolve(str: String): String {
        val matcher = pattern.matcher(str)

        return matcher.replaceAll {
            val matchStr = it.group()
            val variable = matchStr.substring(2, matchStr.length - 2)

            resolveVariable(variable)
        }
    }

    private fun resolveVariable(variable: String): String {
        var innerVariable = resolveInnerVariable(variable)
        if (innerVariable != null) {
            return innerVariable
        }

        val jsScriptExecutor = JsScriptExecutor.getService(project)
        innerVariable = jsScriptExecutor.getRequestVariable(variable)
        if (innerVariable != null) {
            return innerVariable
        }

        innerVariable = jsScriptExecutor.getGlobalVariable(variable)
        if (innerVariable != null) {
            return innerVariable
        }

        val selectedEnv = HttpEditorTopForm.getSelectedEnv(project)
        val envFileService = EnvFileService.getService(project)
        val envValue = envFileService.getEnvValue(variable, selectedEnv)
        if (envValue != null) {
            return envValue
        }

        throw IllegalArgumentException("无法解析变量${variable}")
    }

    private fun resolveInnerVariable(variable: String): String? {
        if (variable == "\$uuid") {
            return UUIDs.compact()
        }

        if (variable == "\$timestamp") {
            return System.currentTimeMillis().toString()
        }

        if (variable == "\$randomInt") {
            return RandomUtils.nextInt(1000).toString()
        }

        if (variable.startsWith("\$random.integer")) {
            val split = variable.split(",")
            val start = patternNotNumber.matcher(split[0]).replaceAll("")
            val end = patternNotNumber.matcher(split[1]).replaceAll("")
            return (start.toInt() + RandomUtils.nextInt(end.toInt())).toString()
        }

        if (variable.startsWith("\$random.alphabetic")) {
            val count = patternNotNumber.matcher(variable).replaceAll("")
            return RandomStringUtils.randomAlphabetic(count.toInt())
        }

        if (variable.startsWith("\$random.alphanumeric")) {
            val count = patternNotNumber.matcher(variable).replaceAll("")
            return RandomStringUtils.randomAlphanumeric(count.toInt())
        }

        if (variable.startsWith("\$random.numeric")) {
            val count = patternNotNumber.matcher(variable).replaceAll("")
            return RandomStringUtils.randomNumeric(count.toInt())
        }

        return null
    }

    companion object {
        fun getService(project: Project): VariableResolver {
            return project.getService(VariableResolver::class.java)
        }
    }
}
