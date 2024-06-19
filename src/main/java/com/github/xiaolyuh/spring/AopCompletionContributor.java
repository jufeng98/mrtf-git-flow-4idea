package com.github.xiaolyuh.spring;

import com.github.xiaolyuh.pcel.parser.PointcutExpressionFile;
import com.github.xiaolyuh.pcel.psi.PointcutExpressionTokenType;
import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionData;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.codeInsight.completion.JavaClassNameCompletionContributor;
import com.intellij.codeInsight.completion.PrefixMatcher;
import com.intellij.codeInsight.completion.util.ParenthesesInsertHandler;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.json.codeinsight.JsonCompletionContributor;
import com.intellij.json.psi.JsonProperty;
import com.intellij.json.psi.JsonStringLiteral;
import com.intellij.patterns.PsiElementPattern;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

import static com.intellij.patterns.PlatformPatterns.psiElement;

public class AopCompletionContributor extends CompletionContributor {
    private static final String[] SUGGESTIONS = {"@annotation", "@target", "execution", "target", "bean"};

    @Override
    public void fillCompletionVariants(@NotNull CompletionParameters parameters, @NotNull CompletionResultSet result) {
        PsiElement position = parameters.getPosition();
//        if (position instanceof LeafPsiElement) {
//            if (((LeafPsiElement) position).getElementType() instanceof PointcutExpressionTokenType) {
//                JavaClassNameCompletionContributor.addAllClasses(parameters, true, result.getPrefixMatcher(), (lookupElement) -> {
//                    if (isNonAnnotation(lookupElement)) {
//                        return;
//                    }
//                    result.addElement(lookupElement);
//                });
//            }
//            return;
//        }
        final int offset = parameters.getOffset();
        String referencePrefix = CompletionData.getReferencePrefix(position, offset);
        String prefixDefault = CompletionData.findPrefixDefault(position, offset, CompletionData.NOT_JAVA_ID);
        String prefixStatic = CompletionData.findPrefixStatic(position, offset);

        PrefixMatcher prefixMatcher = result.getPrefixMatcher();
        String prefix = prefixMatcher.getPrefix();
        System.out.println(prefix);
        PsiElementPattern.Capture<PsiElement> capture = psiElement().withSuperParent(2, PointcutExpressionFile.class);
        if (capture.accepts(position)) {
            for (String suggestion : SUGGESTIONS) {
                LookupElementBuilder bold = LookupElementBuilder.create(suggestion)
                        .withInsertHandler(ParenthesesInsertHandler.WITH_PARAMETERS)
                        .bold();
                result.addElement(bold);
            }
        }

    }

    private static boolean isNonAnnotation(LookupElement element) {
        Object o = element.getObject();
        return o instanceof PsiClass && !((PsiClass) o).isAnnotationType();
    }

}
