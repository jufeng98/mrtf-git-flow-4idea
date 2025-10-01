package com.github.xiaolyuh.provider

import com.github.xiaolyuh.vo.InstanceVo
import com.intellij.openapi.project.Project
import com.intellij.testFramework.LightVirtualFile

/**
 * @author yudong
 */
class ConsoleVirtualFile(
    name: String,
    val selectService: String,
    val instanceVo: InstanceVo,
    val project: Project,
    val mainTest: Boolean,
) :
    LightVirtualFile(name)
