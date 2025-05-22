package com.github.xiaolyuh.startup;

import com.github.xiaolyuh.service.ConfigService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.ProjectActivity;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ProjectStartupActivity implements ProjectActivity {

    @Override
    public @Nullable Object execute(@NotNull Project project, @NotNull Continuation<? super Unit> continuation) {
        ConfigService configService = ConfigService.Companion.getInstance(project);
        configService.tryInitConfig();
        return null;
    }

}
