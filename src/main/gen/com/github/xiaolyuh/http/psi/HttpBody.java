// This is a generated file. Not intended for manual editing.
package com.github.xiaolyuh.http.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface HttpBody extends PsiElement {

  @Nullable
  HttpMultipartContent getMultipartContent();

  @Nullable
  HttpOrdinaryContent getOrdinaryContent();

}