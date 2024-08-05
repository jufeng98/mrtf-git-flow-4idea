package com.github.xiaolyuh.sql.usage;

import com.github.xiaolyuh.sql.psi.SqlNamedElement;
import com.intellij.json.JsonBundle;
import com.intellij.lang.HelpID;
import com.intellij.lang.findUsages.FindUsagesProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SqlFindUsageProvider implements FindUsagesProvider {

    @Override
    public boolean canFindUsagesFor(@NotNull PsiElement psiElement) {
        return psiElement instanceof SqlNamedElement;
    }

    @Override
    public @Nullable @NonNls String getHelpId(@NotNull PsiElement psiElement) {
        return HelpID.FIND_OTHER_USAGES;
    }

    @Override
    public @Nls @NotNull String getType(@NotNull PsiElement element) {
        return "别名 ";
    }

    @Override
    public @Nls @NotNull String getDescriptiveName(@NotNull PsiElement element) {
        if (!(element instanceof PsiNamedElement)) {
            return JsonBundle.message("unnamed.desc");
        }

        PsiNamedElement psiNamedElement = (PsiNamedElement) element;
        String name = psiNamedElement.getName();
        if (name == null) {
            return JsonBundle.message("unnamed.desc");
        }

        return name;
    }

    @Override
    public @Nls @NotNull String getNodeText(@NotNull PsiElement element, boolean useFullName) {
        return getDescriptiveName(element);
    }
}
