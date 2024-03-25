package com.github.xiaolyuh.completion;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import org.jetbrains.annotations.NotNull;

public class GitFlowCompletionContributor extends CompletionContributor {

    @Override
    public void fillCompletionVariants(@NotNull CompletionParameters parameters, @NotNull CompletionResultSet result) {
//        String[] suggestions = {"method1", "method2", "method3"};
//
//        for (String suggestion : suggestions) {
//            LookupElementBuilder lookupElementBuilder = LookupElementBuilder.create(suggestion);
//            result.addElement(lookupElementBuilder);
//        }

//        result.restartCompletionWhenNothingMatches();
    }

}
