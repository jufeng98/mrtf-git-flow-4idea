package com.github.xiaolyuh.inspection;

import com.github.xiaolyuh.i18n.UiBundle;
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
    private static final String UNDERLINE = "_";

    @Override
    public @NotNull PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
        return new JavaElementVisitor() {
            @Override
            public void visitField(PsiField field) {
                String name = field.getName();
                if (name.startsWith(UNDERLINE)) {
                    holder.registerProblem(field, UiBundle.message("inspection.group.names.gitflowplus.fieldname.desc"), fix);
                }
            }
        };
    }

    private static class RemoveUnderlineStartFix implements LocalQuickFix {
        @NotNull
        @Override
        public String getName() {
            return UiBundle.message("inspection.group.names.gitflowplus.fieldname");
        }

        @Override
        public @IntentionFamilyName @NotNull String getFamilyName() {
            return getName();
        }

        @Override
        public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
            PsiField psiField = (PsiField) descriptor.getPsiElement();
            RefactoringFactory factory = RefactoringFactory.getInstance(project);
            String newFieldName = getNewFieldName(psiField.getName());
            RenameRefactoring rename = factory.createRename(psiField, newFieldName);
            rename.run();
        }

        @Override
        public boolean startInWriteAction() {
            return false;
        }

        private String getNewFieldName(String fieldName) {
            char[] charArray = fieldName.toCharArray();
            int index = 0;
            for (int i = 0; i < charArray.length; i++) {
                char c = charArray[i];
                if (c != UNDERLINE.charAt(0)) {
                    index = i;
                    break;
                }
            }
            return fieldName.substring(index);
        }
    }
}
