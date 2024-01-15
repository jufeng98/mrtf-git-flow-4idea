package com.github.xiaolyuh.ui;

import com.github.xiaolyuh.config.InitOptions;
import com.github.xiaolyuh.service.GitFlowPlus;
import com.github.xiaolyuh.utils.ConfigUtil;
import com.github.xiaolyuh.utils.StringUtils;
import com.github.xiaolyuh.vo.BranchVo;
import com.github.xiaolyuh.vo.DeleteBranchOptions;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import git4idea.repo.GitRepository;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author yudong
 */
public class BranchDeleteDialog extends DialogWrapper {
    private final DeleteBranchOptions deleteBranchOptions;
    private final GitFlowPlus gitFlowPlus;
    private static final String[] COLUMN_NAMES = {"序号", "最后一次提交时间", "分支名称", "创建人"};
    private static List<BranchVo> branchVos;

    private JPanel mainPanel;
    private JTable branchTable;
    private JComboBox<String> branchModel;
    private JCheckBox isDeleteLocalBranchBox;
    private JButton searchButton;
    private JTextField branchNameField;
    private JTextField deleteBeforeDate;

    @SuppressWarnings("DataFlowIssue")
    public BranchDeleteDialog(@Nullable GitRepository repository) {
        super(repository.getProject(), true);
        deleteBranchOptions = new DeleteBranchOptions();
        gitFlowPlus = GitFlowPlus.getInstance();
        setTitle("Branch Delete");
        setOKButtonText("Delete");
        searchButton.addActionListener((actionEvent) -> refreshBranchList(repository));

        init();

        refreshBranchList(repository);
    }

    public void refreshBranchList(GitRepository repository) {
        Task.Modal task = new Task.Modal(repository.getProject(), mainPanel, "Loading......", true) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                InitOptions initOptions = ConfigUtil.getInitOptions(repository.getProject());

                List<BranchVo> branchVoList = getBranchListFiltered(repository);

                String selectedItem = (String) branchModel.getSelectedItem();
                //noinspection DataFlowIssue
                switch (selectedItem) {
                    case "已上线分支":
                        List<String> mergedBranches = gitFlowPlus.getMergedBranchList(repository);
                        branchVoList = branchVoList.stream()
                                .filter((branchVo) -> mergedBranches.contains(branchVo.getBranch()))
                                .collect(Collectors.toList());
                        break;
                    case "全部开发分支":
                        branchVoList = branchVoList.stream()
                                .filter((branchVo) -> {
                                    String branch = branchVo.getBranch();
                                    return branch.contains(initOptions.getFeaturePrefix())
                                            || branch.contains(initOptions.getHotfixPrefix());
                                })
                                .collect(Collectors.toList());
                        break;
                    default:
                }

                List<BranchVo> finalBranchVoList = branchVoList;
                ApplicationManager.getApplication().invokeLater(() -> renderingBranchTable(finalBranchVoList));
            }
        };
        ProgressManager.getInstance().run(task);
    }

    private void renderingBranchTable(List<BranchVo> branches) {
        branchVos = branches;

        Object[][] rowData = new Object[branches.size()][4];
        for (int i = 0; i < branches.size(); ++i) {
            BranchVo branchVo = branches.get(i);
            branchVo.setId(i);
            rowData[i][0] = i + 1;
            rowData[i][1] = DateTimeFormatter.ofPattern("yyyy-MM-dd").format(branchVo.getLastCommitDate().toInstant());
            rowData[i][2] = branchVo.getBranch();
            rowData[i][3] = branchVo.getCreateUser();
        }

        TableModel dataModel = new DefaultTableModel(rowData, COLUMN_NAMES) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        branchTable.setModel(dataModel);
        branchTable.updateUI();
    }

    private List<BranchVo> getBranchListFiltered(GitRepository repository) {
        List<BranchVo> allBranches = gitFlowPlus.getBranchList(repository);

        if (StringUtils.isNotBlank(branchNameField.getText())) {
            allBranches = allBranches.stream()
                    .filter((branchVo) -> branchVo.getBranch().contains(branchNameField.getText()))
                    .collect(Collectors.toList());
        }

        if (StringUtils.isNotBlank(deleteBeforeDate.getText())) {
            Instant instant = Instant.now().minus(-Integer.parseInt(deleteBeforeDate.getText().trim()), ChronoUnit.DAYS);
            Date date = Date.from(instant);
            allBranches = allBranches.stream()
                    .filter((branchVo) -> branchVo.getLastCommitDate().compareTo(date) > 0)
                    .collect(Collectors.toList());
        }

        return allBranches;
    }

    protected void doOKAction() {
        int[] selectedRows = branchTable.getSelectedRows();
        if (selectedRows.length == 0) {
            //noinspection DialogTitleCapitalization
            JBPopupFactory.getInstance().createMessage("至少选择一行需要删除的分支！").showInCenterOf(branchTable);
            return;
        }
        List<Integer> list = Arrays.stream(selectedRows).boxed().toList();

        List<BranchVo> tmpList = branchVos.stream()
                .filter(it -> list.contains(it.getId()))
                .collect(Collectors.toList());

        deleteBranchOptions.setBranches(tmpList);
        deleteBranchOptions.setDeleteLocalBranch(isDeleteLocalBranchBox.isSelected());
        super.doOKAction();
    }

    public void doCancelAction() {
        super.doCancelAction();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return mainPanel;
    }

    public DeleteBranchOptions getDeleteBranchOptions() {
        return deleteBranchOptions;
    }

}
