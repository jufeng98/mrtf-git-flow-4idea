package com.github.xiaolyuh.inspection;

import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaElementVisitor;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiField;
import com.intellij.refactoring.RefactoringFactory;
import com.intellij.refactoring.RenameRefactoring;
import org.jetbrains.annotations.NotNull;

public class FieldNameInspection extends AbstractBaseJavaLocalInspectionTool {
    private final RemoveUnderlineStartFix fix = new RemoveUnderlineStartFix();

    @Override
    public @NotNull PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
        return new JavaElementVisitor() {
            @Override
            public void visitField(PsiField field) {
                String name = field.getName();
                if (name.startsWith("_")) {
                    holder.registerProblem(field, "", fix);
                }
            }
        };
    }

    private static class RemoveUnderlineStartFix implements LocalQuickFix {
        @NotNull
        @Override
        public String getName() {
            return "去掉下划线";
        }

        @Override
        public @IntentionFamilyName @NotNull String getFamilyName() {
            return getName();
        }

        @Override
        public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
            PsiField psiField = (PsiField) descriptor.getPsiElement();
            RefactoringFactory factory = RefactoringFactory.getInstance(project);
            RenameRefactoring rename = factory.createRename(psiField, psiField.getName().replace("_", ""));
            rename.run();
        }
    }
}
