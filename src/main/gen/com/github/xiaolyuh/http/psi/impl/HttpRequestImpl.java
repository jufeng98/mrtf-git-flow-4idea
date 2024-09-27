// This is a generated file. Not intended for manual editing.
package com.github.xiaolyuh.http.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static com.github.xiaolyuh.http.psi.HttpTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.github.xiaolyuh.http.psi.*;

public class HttpRequestImpl extends ASTWrapperPsiElement implements HttpRequest {

  public HttpRequestImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull HttpVisitor visitor) {
    visitor.visitRequest(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof HttpVisitor) accept((HttpVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public HttpBody getBody() {
    return findChildByClass(HttpBody.class);
  }

  @Override
  @Nullable
  public HttpHeaders getHeaders() {
    return findChildByClass(HttpHeaders.class);
  }

  @Override
  @NotNull
  public HttpMethod getMethod() {
    return findNotNullChildByClass(HttpMethod.class);
  }

  @Override
  @Nullable
  public HttpOutputFile getOutputFile() {
    return findChildByClass(HttpOutputFile.class);
  }

  @Override
  @Nullable
  public HttpScript getScript() {
    return findChildByClass(HttpScript.class);
  }

  @Override
  @Nullable
  public HttpUrl getUrl() {
    return findChildByClass(HttpUrl.class);
  }

  @Override
  @Nullable
  public HttpVersion getVersion() {
    return findChildByClass(HttpVersion.class);
  }

}
