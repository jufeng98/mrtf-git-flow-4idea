package com.github.xiaolyuh.pcel.psi;

import com.github.xiaolyuh.pcel.PointcutExpressionLanguage;
import com.github.xiaolyuh.pcel.parser.PointcutExpressionFile;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.util.PsiTreeUtil;

public class PointcutExpressionElementFactory {
    public static AopExpr createAtAnnoExpr(Project project, String expr) {
        String text = "@annotation" + expr;
        final PointcutExpressionFile file = createFile(project, text);
        return PsiTreeUtil.findChildOfType(file.getFirstChild(), AopExpr.class);
    }

    public static PointcutExpressionFile createFile(Project project, String text) {
        String name = "dummy.pcel";
        return (PointcutExpressionFile) PsiFileFactory.getInstance(project).createFileFromText(name, PointcutExpressionLanguage.INSTANCE, text);
    }
}
