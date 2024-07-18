package com.github.xiaolyuh.start;

import com.dbn.browser.DatabaseBrowserManager;
import com.dbn.cache.MetadataCacheService;
import com.dbn.connection.ConnectionHandler;
import com.github.xiaolyuh.dbn.DbnToolWindowPsiElement;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
import org.jetbrains.annotations.NotNull;

public class DbnProjectStartupActivity implements StartupActivity {
    @Override
    public void runActivity(@NotNull Project project) {
        DatabaseBrowserManager browserManager = DatabaseBrowserManager.getInstance(project);
        ConnectionHandler connection = browserManager.getSelectedConnection();
        if (!ConnectionHandler.isLiveConnection(connection)) {
            return;
        }

        String dbName = DbnToolWindowPsiElement.Companion.getFirstConnectionConfigDbName(project);
        if (dbName == null) {
            return;
        }

        MetadataCacheService cacheService = MetadataCacheService.getService(project);
        // 从本地缓存中初始化数据库元数据信息
        cacheService.initCacheDbTable(dbName, project, connection.getConnectionId().id());
    }
}
