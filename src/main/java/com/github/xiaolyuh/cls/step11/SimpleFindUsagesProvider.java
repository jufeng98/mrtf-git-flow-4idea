package com.github.xiaolyuh.cls.step11;

import com.github.xiaolyuh.cls.psi.SimpleProperty;
import com.github.xiaolyuh.cls.SimpleLexerAdapter;
import com.github.xiaolyuh.cls.step4.SimpleTokenSets;
import com.github.xiaolyuh.cls.step7.SimpleAnnotator;
import com.intellij.lang.cacheBuilder.DefaultWordsScanner;
import com.intellij.lang.cacheBuilder.WordsScanner;
import com.intellij.lang.findUsages.FindUsagesProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class SimpleFindUsagesProvider implements FindUsagesProvider {

    @Override
    public @NotNull WordsScanner getWordsScanner() {
        return new DefaultWordsScanner(new SimpleLexerAdapter(),
                SimpleTokenSets.IDENTIFIERS,
                SimpleTokenSets.COMMENTS,
                TokenSet.EMPTY);
    }

    @Override
    public boolean canFindUsagesFor(@NotNull PsiElement psiElement) {
        return psiElement instanceof PsiNamedElement;
    }

    @Nullable
    @Override
    public String getHelpId(@NotNull PsiElement psiElement) {
        return null;
    }

    @NotNull
    @Override
    public String getType(@NotNull PsiElement element) {
        if (element instanceof SimpleProperty) {
            return "simple property";
        }
        return "";
    }

    @NotNull
    @Override
    public String getDescriptiveName(@NotNull PsiElement element) {
        if (element instanceof SimpleProperty) {
            return ((SimpleProperty) element).getKey();
        }
        return "";
    }

    @NotNull
    @Override
    public String getNodeText(@NotNull PsiElement element, boolean useFullName) {
        if (element instanceof SimpleProperty) {
            return ((SimpleProperty) element).getKey() +
                    SimpleAnnotator.SIMPLE_SEPARATOR_STR +
                    ((SimpleProperty) element).getValue();
        }
        return "";
    }

}
