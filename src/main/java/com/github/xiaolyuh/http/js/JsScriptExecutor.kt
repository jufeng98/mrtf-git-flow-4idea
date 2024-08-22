package com.github.xiaolyuh.http.js

import com.github.xiaolyuh.utils.HttpClientUtil
import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import java.net.http.HttpResponse
import java.nio.charset.StandardCharsets
import javax.script.Invocable
import javax.script.ScriptEngine
import javax.script.ScriptEngineManager


@Service(Service.Level.PROJECT)
class JsScriptExecutor {
    private var manager: ScriptEngineManager = ScriptEngineManager()
    private var engine: ScriptEngine = manager.getEngineByName("JavaScript")

    init {
        engine.eval(
            """
                client = {
                  fullMsg: '',
                  log: function(msg) {
                    this.fullMsg = this.fullMsg + '# ' + msg + '\r\n';
                  },
                  global: {
                    get: function(key) {
                      return this[key];
                    },
                    set: function(key,val) {
                      this[key] = val;
                      client.log(key + ' 已设置为: ' + val);
                    }
                  },
                  test: function(successMsg, assertCallback) {
                    var success = assertCallback();                    
                    if(success === true || success === undefined) {                   
                        this.log(successMsg);
                    }
                  },
                  assert: function(success, failMsg) {
                    if(!success) {                      
                      this.log("断言失败: " + failMsg);
                    }
                    return success;
                  }                
                }
                function getLog() {
                    var tmp = client.fullMsg;
                    client.fullMsg = '';
                    return tmp;
                }
                function getVariable(key) {
                    return client.global.get(key);
                }
        """.trimIndent()
        )
    }

    fun evalJsBeforeRequest(beforeJsScripts: List<String>): List<String> {
        if (beforeJsScripts.isEmpty()) {
            return arrayListOf()
        }

        val resList = mutableListOf("# 前置js执行结果:\r\n")

        val list = beforeJsScripts
            .map { evalJs(it) }
            .filter { it.isNotEmpty() }

        resList.addAll(list)
        return resList
    }

    fun evalJsAfterRequest(
        jsScript: String?,
        response: HttpResponse<ByteArray>,
        resPair: Pair<String, ByteArray>
    ): String? {
        if (jsScript == null) {
            return null
        }

        val headerJsonStr = HttpClientUtil.gson.toJson(response.headers().map())

        if (resPair.first != "image") {
            val bytes = resPair.second
            val jsonStr = String(bytes, StandardCharsets.UTF_8)
            val js = """
                      response = {
                        status: ${response.statusCode()},
                        body: $jsonStr,
                        headers: $headerJsonStr
                      };
                      $jsScript;
            """.trimIndent()

            return evalJs(js)
        }

        return null
    }

    private fun evalJs(jsScript: String?): String {
        engine.eval(jsScript)

        val invocable = engine as Invocable
        return invocable.invokeFunction("getLog") as String
    }

    fun getGlobalVariable(key: String): String? {
        val invocable = engine as Invocable
        return invocable.invokeFunction("getVariable", key) as String?
    }

    companion object {
        fun getService(project: Project): JsScriptExecutor {
            return project.getService(JsScriptExecutor::class.java)
        }
    }
}
