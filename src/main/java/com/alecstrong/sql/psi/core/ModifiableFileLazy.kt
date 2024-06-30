package com.alecstrong.sql.psi.core

import com.intellij.psi.PsiFile
import kotlin.reflect.KProperty

internal class ModifiableFileLazy<out T>(
    private val file: PsiFile,
    private val initializer: () -> T
) {
    private var modifiedStamp = file.modificationStamp
    private var state: T? = null

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        if (file.modificationStamp == modifiedStamp) {
            state?.let { return it }
        }

        synchronized(this) {
            if (file.modificationStamp == modifiedStamp) {
                state?.let { return it }
            }

            val state = initializer()
            this.state = state
            modifiedStamp = file.modificationStamp
            return state
        }
    }
}