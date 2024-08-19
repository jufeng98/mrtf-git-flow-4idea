package com.github.xiaolyuh.http;

import com.intellij.lang.Language;

public class HttpLanguage extends Language {

    public static final HttpLanguage INSTANCE = new HttpLanguage();

    private HttpLanguage() {
        super("http");
    }

}
