package com.github.xiaolyuh.utils;

import java.util.Arrays;
import java.util.stream.Collectors;

public class ExceptionUtils {
    private static final String BIZ_PACKAGE_NAME = "com.github";

    public static String getStackTrace(Throwable t) {
        String error = getTraceStr(t);

        Throwable cause = t.getCause();
        if (cause == null) {
            return error;
        }

        String causeError = getCauseTraceStr(cause);

        error = error + causeError;

        return error;
    }

    private static String getCauseTraceStr(Throwable cause) {
        String traceStr = getTraceStr(cause);

        String str = " Caused by:" + traceStr;

        Throwable cause1 = cause.getCause();
        if (cause1 != null) {
            str += getCauseTraceStr(cause1);
        }

        return str;
    }

    private static String getTraceStr(Throwable t) {
        String name = t.getClass().getPackage().getName();

        StackTraceElement[] stackTrace = t.getStackTrace();

        String trace;

        if (name.startsWith(BIZ_PACKAGE_NAME)) {
            trace = Arrays.stream(stackTrace)
                    .filter(it -> it.getLineNumber() != -1 && it.getClassName().startsWith(BIZ_PACKAGE_NAME))
                    .limit(8)
                    .map(it -> " at " + it)
                    .collect(Collectors.joining(" "));

        } else {
            trace = Arrays.stream(stackTrace)
                    .limit(8)
                    .map(it -> " at " + it)
                    .collect(Collectors.joining(" "));
        }

        return t + " " + trace;
    }

}
