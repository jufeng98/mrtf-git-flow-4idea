# 简介
GitFlowTools 插件是一款基于[mrtf-git-flow](https://xiaolyuh.blog.csdn.net/article/details/105180250)分支管理流程的Idea插件，它最主要的作用是用来简化分支管理流程，最大限度的防止误操作。
Fork code from [GitFlowPlus](https://plugins.jetbrains.com/plugin/14056-gitflowplus).

#### mrtf说明
![mrrtf.png](images/mrrtf.png)

> 在初始化插件之前必须先保证仓库中具有```origin/master```分支。

主要功能如下：
- 插件配置文件可以加入GIT版本管理，在团队内部共享
- 基于```origin/master```新建开发分支和修复分支
- 基于```origin/master```重建测试分支和发布分支
- 开发完成后将开发分支合并到测试分支
- 支持在本地发起Merge Request
- 测试完成后将开发分支合并到发布分支，并锁定发布分支
- 发布完成后将发布分支合并到```origin/master```分支
- 发布失败将解除发布分支的锁定
- 只有锁定发布分支的人才能点[发布完成]和[发布失败]
- 所有执行的git命令都可以在"Event Log"查看
- 支持在本地发起Merge Request
- 批量删除分支
- 提测时可选择触发 Kubesphere 流水线
- 触发 Kubesphere 流水线后可检测服务的构建和启动情况并通知进度
- 连接 Kubesphere pod 控制台
- 查看 Kubesphere pod 日志

# 主要解决的问题
1. 新建特性分支操作过程复杂，且容易出错
2. 提测等环节合并代码出错，老是将测试分支代码带上线
3. 解决多人同时发布，将未完成预发布测试的代码带上线
4. 解决发布完成后忘记将代码同步到```origin/master```分支
5. 发布完成后忘记打Tag
6. 修改代码后部署 Kubesphere 测试环境的繁琐性

# 安装
## 在线安装
![local_install.gif](images/online_install.gif)


## 离线安装
![local_install.gif](images/local_install.gif)

# 插件入口
![插件入口.png](images/main.png)

插件入口有2个：
1. 在Toolbar栏，这个需要显示Toolbar（View->Toolbar）
2. 在Statusbar中

# 配置管理
每个仓库都需要进行插件初始化，配置完成后会生成一个```git-flow-plus.config```配置文件，该文件可以添加到git版本管理中进行组内同步，同步完成后组内成员可以共享配置。

![init.gif](images/init.gif)


如果配置了钉钉机器人Token，那么在点击[开始发布]的时候，钉钉机器人会在钉钉群发布一条发布分支被锁定的消息，格式如下：
```
xxx 服务发布分支已被锁定，最后一次操作：

  操作人: yuhao.wang3@xxx.com;

  时间: 2020-03-27_16:38:09;

  Message: 初始化插件配置 ;

如需强行发布，请先点[发布失败]解除锁定，再点[开始发布]。
```

# 新建分支
新建开发分支和修复分支都会直接从```origin/master```新建分支，新建分支后会自动切换到新建后的分支。
![new_branch.gif](images/new_branch.gif)

执行命令：
```git
git fetch origin master:newBranchName
git checkout newBranchName --force
git push origin newBranchName:newBranchName --tag  --set-upstream
```

> 如果本地有修改文件未提交是不允许新建和重建分支的


# 重建测试分支
重建测试分支会直接从```origin/master```新建分支一个测试分支，原来的测试分支会被直接删除。

执行命令：
```git
git checkout master --force
git push origin --delete rebuildBranchName
git branch -D rebuildBranchName

git fetch origin master:rebuildBranchName
git checkout rebuildBranchName --force
git push origin rebuildBranchName:rebuildBranchName --tag  --set-upstream
```

# 重建发布分支
重建发布分支会直接从```origin/master```新建分支一个发布分支，原来的发布分支会被直接删除。
> 如果当前的发布分支处于锁定状态，那么将不允许重建发布分支。

执行命令和重建测试分支一样。

# 提测
提测会将当前分支合并到```origin/test```，在合并过程中如果出现冲突并且选择未解决，那么当前分支会切换到本地```test分支```，等待解决冲突；如果没有任何异常情况，那么合并完成后当前分支不会发生切换。
> 当前分支必须是开发分支或者修复分支时，才允许提测。
![test.gif](images/test.gif)

执行命令：
测试分支在远程和本地都不存在，会新建测试分支：
```
git fetch origin master:test
git checkout test --force
git push origin test:test --tag  --set-upstream
```
测试分支在本地不存在，会在本地新建测试分支：
```
git checkout -b test origin/test
```

测试分支存在：
```git
git checkout test --force
git pull origin test:test
git megre featureBranchName (如果没解决冲突后面语句不执行)
git push origin test:test --tag  --set-upstream
git checkout featureBranchName --force
```

# 开始发布
开始发布会将当前分支合并到```origin/release```，并且锁定发布分支，如果配置了钉钉的机器人Token，那么还会往钉钉群发送一条发布分支锁定消息。

发布分支一旦锁定后，其他人将不能再进行发布，如果确实需要发布有两种解决方式：
1. 让第一个发布人点发布完成，发布完成会将发布分支合并到```origin/master```，并解除发布分支锁定。
2. 让第一个发布人点发布失败，发布失败将直接解除发布分支锁定。

![release.gif](images/release.gif)

执行命令：
加锁
```git
git push origin featureBranchName:GFP_LOCK_BRANCH_NAME  (返回 new branch 则成功获取锁)
```
获取最后一次操作人：
```git
git show  origin/release -s --format=Author:%ae-Date:%ad-Message:%s --date=format:%Y-%m-%d_%H:%M:%S
```

发布分支在远程和本地都不存在，会新建发布分支：
```
git fetch origin master:release
git checkout release --force
git push origin release:release --tag  --set-upstream
```
发布分支在本地不存在，会在本地新建发布分支：
```
git checkout -b release origin/release
```

发布分支存在：
```git
git checkout release --force 
git pull origin release:release
git megre featureBranchName (如果没解决冲突后面语句不执行)
git push origin release:release --tag  --set-upstream
git checkout featureBranchName --force
```

# 发布完成
发布完成会将发布分支合并到```origin/master```，并解除发布分支锁定，必须打Tag；
> 只有发布分支处于锁定状态，该按钮才可用

![finish_release.gif](images/finish_release.gif)

执行命令：
先fetch在判断发布分支是否处于锁定状态：
```git
git fetch origin 
```

主干分支在本地不存在，会在本地新建主干分支：
```
git checkout -b master origin/master
```

主干分支存在：
```git
git checkout master --force
git pull origin master:master
git megre featureBranchName (如果没解决冲突后面语句不执行)
git tag -a -f -m message tagName
git push origin master:master --tag  --set-upstream 
git checkout featureBranchName --force 
git push origin --delete GFP_LOCK_BRANCH_NAME（解锁）
```

# 发布失败
直接解除发布分支锁定。
> 只有发布分支处于锁定状态，该按钮才可用

![failure_release.gif](images/failure_release.gif)

执行命令：
先fetch在判断发布分支是否处于锁定状态：
```git
git fetch origin （更新本地仓库缓存）
```

解锁
```git
git push origin --delete GFP_LOCK_BRANCH_NAME（解锁）
```

# 帮助
点击帮助会直接跳转插件首页

# 批量删除分支
![delete-branches.png](images/delete-branches.png)

# 查看 Kubesphere 远程服务实例日志
![Kubesphere-log.png](images/kubesphere-log.png)

# 连接 Kubesphere 远程服务实例控制台
![Kubesphere-console.png](images/kubesphere-console.png)

# 作者信息
- 作者博客：[https://www.zhihu.com/people/liang-yu-dong-44](https://www.zhihu.com/people/liang-yu-dong-44)
- 作者邮箱： 375709770@qq.com  
- github 地址：[https://github.com/jufeng98](https://github.com/jufeng98)

# 捐赠
如果项目帮到了您，请作者喝杯咖啡吧！
![donate.jpg](images/donate.jpg)

# 技术支持
添加微信记得备注 ```GitFlowTools```。
![wechat.jpg](images/wechat.jpg)




