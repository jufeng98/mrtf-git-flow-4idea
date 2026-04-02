package com.github.xiaolyuh.ui;

import com.github.xiaolyuh.i18n.I18n;
import com.github.xiaolyuh.service.GitFlowPlus;
import com.github.xiaolyuh.utils.StringUtils;
import com.github.xiaolyuh.vo.DeleteTagOptions;
import com.github.xiaolyuh.vo.TagVo;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.*;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import git4idea.repo.GitRepository;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.table.*;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.github.xiaolyuh.consts.Constants.DATE_PATTERN_FULL;

/**
 * @author yudong
 */
public class TagDeleteDialog extends DialogWrapper {
    private static final String[] COLUMN_NAMES = I18n.getContent("tag.column.name").split(",");
    private final DeleteTagOptions deleteTagOptions;
    private final GitFlowPlus gitFlowPlus;
    private static List<TagVo> tagVos;

    private JPanel mainPanel;
    private JTable tagTable;
    private JButton searchButton;
    private JTextField tagNameField;
    private JTextField deleteBeforeDate;

    public TagDeleteDialog(GitRepository repository) {
        super(repository.getProject(), true);

        deleteTagOptions = new DeleteTagOptions();

        gitFlowPlus = GitFlowPlus.getInstance();

        setTitle(I18n.nls("DeleteTagAction.text"));
        setOKButtonText(I18n.nls("delete"));

        searchButton.addActionListener((actionEvent) -> refreshTagList(repository));

        init();

        refreshTagList(repository);
    }

    public void refreshTagList(GitRepository repository) {
        Task.Modal task = new Task.Modal(repository.getProject(), mainPanel, "Loading......", true) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                List<TagVo> tagVoList = getTagListFiltered(repository);

                ApplicationManager.getApplication().invokeLater(() -> renderingBranchTable(tagVoList));
            }
        };
        ProgressManager.getInstance().run(task);
    }

    private void renderingBranchTable(List<TagVo> tags) {
        tagVos = tags;

        Object[][] rowData = new Object[tags.size()][4];
        for (int i = 0; i < tags.size(); ++i) {
            TagVo tagVo = tags.get(i);
            tagVo.setId(i);
            rowData[i][0] = i + 1;

            rowData[i][1] = DateFormatUtils.format(tagVo.getCreateDate(), DATE_PATTERN_FULL);
            rowData[i][2] = tagVo.getTag();
            rowData[i][3] = tagVo.getCreateUser();
        }

        TableModel dataModel = new DefaultTableModel(rowData, COLUMN_NAMES) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tagTable.setModel(dataModel);

        TableColumnModel columnModel = tagTable.getColumnModel();
        int width = 80;
        columnModel.getColumn(0).setWidth(width);
        columnModel.getColumn(0).setPreferredWidth(width);
        columnModel.getColumn(0).setMaxWidth(width);

        tagTable.updateUI();
    }

    private List<TagVo> getTagListFiltered(GitRepository repository) {
        List<TagVo> allTags = gitFlowPlus.getTagDetailList(repository);

        if (StringUtils.isNotBlank(tagNameField.getText())) {
            allTags = allTags.stream()
                    .filter((it) -> it.getTag().contains(tagNameField.getText()))
                    .collect(Collectors.toList());
        }

        if (StringUtils.isNotBlank(deleteBeforeDate.getText())) {
            Date date = Date.from(ZonedDateTime.now().minusDays(Integer.parseInt(deleteBeforeDate.getText().trim())).toInstant());

            allTags = allTags.stream()
                    .filter((branchVo) -> branchVo.getCreateDate().compareTo(date) > 0)
                    .collect(Collectors.toList());
        }

        return allTags;
    }

    protected void doOKAction() {
        int[] selectedRows = tagTable.getSelectedRows();

        if (selectedRows.length == 0) {
            JBPopupFactory.getInstance()
                    .createMessage(I18n.getContent("at.least.one.tag"))
                    .showInCenterOf(tagTable);
            return;
        }

        List<Integer> list = Arrays.stream(selectedRows).boxed().toList();

        List<TagVo> tmpList = tagVos.stream()
                .filter(it -> list.contains(it.getId()))
                .collect(Collectors.toList());

        deleteTagOptions.setTags(tmpList);

        super.doOKAction();
    }

    public void doCancelAction() {
        super.doCancelAction();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return mainPanel;
    }

    public DeleteTagOptions getDeleteTagOptions() {
        return deleteTagOptions;
    }

}
