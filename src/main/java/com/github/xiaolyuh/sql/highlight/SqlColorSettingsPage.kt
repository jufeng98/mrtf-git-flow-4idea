package com.github.xiaolyuh.sql.highlight

import com.github.xiaolyuh.sql.SqlIcons
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighter
import com.intellij.openapi.options.colors.AttributesDescriptor
import com.intellij.openapi.options.colors.ColorDescriptor
import com.intellij.openapi.options.colors.ColorSettingsPage
import javax.swing.Icon

class SqlColorSettingsPage : ColorSettingsPage {
    private val attributeDescriptors: MutableList<AttributesDescriptor> = ArrayList(20)

    init {
        attributeDescriptors.add(AttributesDescriptor("Comment", SqlSyntaxHighlighter.COMMENT))
        attributeDescriptors.add(AttributesDescriptor("String", SqlSyntaxHighlighter.STRING))
        attributeDescriptors.add(AttributesDescriptor("Number", SqlSyntaxHighlighter.NUMBER))
        attributeDescriptors.add(AttributesDescriptor("Identifier", SqlSyntaxHighlighter.IDENTIFIER))
        attributeDescriptors.add(AttributesDescriptor("Keyword", SqlSyntaxHighlighter.KEYWORD))
    }

    override fun getIcon(): Icon {
        return SqlIcons.FILE
    }

    override fun getHighlighter(): SyntaxHighlighter {
        return SqlSyntaxHighlighter()
    }

    override fun getDemoText(): String {
        return """
            /*
             * Name: GitFlowPlus
             * Version: 2.0
             * SQL demo text
             */
            
            CREATE TABLE mall_sys_dict_entry (
              DICTTYPEID varchar(50) NOT NULL COMMENT '业务字典子选项',
              DICTID varchar(100) NOT NULL COMMENT '业务字典子选项编号',
              DICTNAME varchar(200) DEFAULT NULL COMMENT '业务字典子选项名称',
              STATUS int(10) DEFAULT NULL COMMENT '状态（1使用中/0已废弃）',
              SORTNO int(10) DEFAULT NULL COMMENT '排序编码',
              PRIMARY KEY (DICTTYPEID,DICTID)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='字典表';
                              
            -- insert data
            INSERT INTO
              mall_sys_dict_entry (DICTTYPEID, DICTID, DICTNAME, STATUS, SORTNO)
            VALUES
              ('ACTION_TYPE', '1', '沟通提报', 1, 1);
           
            -- select data
            select
                msde.DICTID,
                msde.DICTNAME,
                msde.DICTTYPEID as dictTypeId,
                msde.SORTNO     as sortNo,
                msde.STATUS
            from
                mall_sys_dict_entry msde
            where 
                msde.DICTTYPEID = 'ACTION_TYPE'
            order by
                msde.SORTNO desc;        
        """.trimIndent()
    }

    override fun getAdditionalHighlightingTagToDescriptorMap(): Map<String, TextAttributesKey>? {
        return null
    }

    override fun getAttributeDescriptors(): Array<AttributesDescriptor> {
        return attributeDescriptors.toTypedArray<AttributesDescriptor>()
    }

    override fun getColorDescriptors(): Array<ColorDescriptor> {
        return ColorDescriptor.EMPTY_ARRAY
    }

    override fun getDisplayName(): String {
        return "Sql"
    }

}
