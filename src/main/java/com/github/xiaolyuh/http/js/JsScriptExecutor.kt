package com.github.xiaolyuh.http.js

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
    private var engine: ScriptEngine = manager.getEngineByName("nashorn")

    init {
        engine.eval(
            """
                client = {
                  fullMsg: '',
                  log: function(msg) {
                    this.fullMsg = this.fullMsg + msg + '\r\n';
                  },
                  global: {
                    get: function(key) {
                      return this[key];
                    },
                    set: function(key,val) {
                      this[key] = val;
                      client.log(key + '已设置为:' + val);
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
                    return success
                  }                
                }
                function getLog() {
                    var tmp = client.fullMsg
                    client.fullMsg = ''
                    return tmp
                }
        """.trimIndent()
        )
    }

    fun evalJs(jsScript: String?, response: HttpResponse<ByteArray>, resObj: Any): Any? {
        if (jsScript == null) {
            return null
        }

        if (resObj is Pair<*, *> && resObj.first == "json") {
            val bytes = resObj.second as ByteArray
            val jsonStr = String(bytes,StandardCharsets.UTF_8)
            val js = """
                      response = {
                        status: ${response.statusCode()},
                        body: $jsonStr,
                        headers: {}
                      };
                      $jsScript;
            """.trimIndent()

            engine.eval(js)

            val invocable = engine as Invocable
            return invocable.invokeFunction("getLog")
        }

        return null
    }

    companion object {
        fun getService(project: Project): JsScriptExecutor {
            return project.getService(JsScriptExecutor::class.java)
        }
    }
}
