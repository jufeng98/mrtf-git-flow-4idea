// This is a generated file. Not intended for manual editing.
package com.github.xiaolyuh.http.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static com.github.xiaolyuh.http.psi.HttpTypes.*;
import com.github.xiaolyuh.http.inject.HttpPsiLanguageInjectionHost;
import com.github.xiaolyuh.http.psi.*;

public class HttpScriptImpl extends HttpPsiLanguageInjectionHost implements HttpScript {

  public HttpScriptImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull HttpVisitor visitor) {
    visitor.visitScript(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof HttpVisitor) accept((HttpVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public PsiElement getJsScript() {
    return findNotNullChildByType(JS_SCRIPT);
  }

}
