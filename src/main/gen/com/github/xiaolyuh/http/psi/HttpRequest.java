// This is a generated file. Not intended for manual editing.
package com.github.xiaolyuh.http.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface HttpRequest extends PsiElement {

  @Nullable
  HttpBody getBody();

  @Nullable
  HttpHeaders getHeaders();

  @Nullable
  HttpMethod getMethod();

  @Nullable
  HttpScript getScript();

  @Nullable
  HttpUrl getUrl();

  @Nullable
  HttpVersion getVersion();

}
