package sorm.bean;

import java.util.List;
import java.util.Map;

/**
 * 存储表结构的信息
 * @author wallace
 */
public class TableInfo {
    /**
     * 表名
     */
    private String t_name;
    /**
     * 所有字段的信息
     */
    private Map<String,ColumnInfo> columns;
    /**
     * 唯一主键（目前只处理表中只有一个主键的情况）
     */
    private ColumnInfo primaryKey;
    /**
     * 联合主键
     */
    private List<ColumnInfo> primaryKeys;

    public TableInfo(String t_name, Map<String, ColumnInfo> columns, ColumnInfo primaryKey) {
        this.t_name = t_name;
        this.columns = columns;
        this.primaryKey = primaryKey;
    }

    public TableInfo(String t_name, Map<String, ColumnInfo> columns, List<ColumnInfo> primaryKeys) {
        this.t_name = t_name;
        this.columns = columns;
        this.primaryKeys = primaryKeys;
    }

    public TableInfo(String t_name, Map<String, ColumnInfo> columns, ColumnInfo primaryKey, List<ColumnInfo> primaryKeys) {
        this.t_name = t_name;
        this.columns = columns;
        this.primaryKey = primaryKey;
        this.primaryKeys = primaryKeys;
    }

    public TableInfo() {
    }

    public String getT_name() {
        return t_name;
    }

    public void setT_name(String t_name) {
        this.t_name = t_name;
    }

    public Map<String, ColumnInfo> getColumns() {
        return columns;
    }

    public void setColumns(Map<String, ColumnInfo> columns) {
        this.columns = columns;
    }

    public ColumnInfo getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(ColumnInfo primaryKey) {
        this.primaryKey = primaryKey;
    }

    public List<ColumnInfo> getPrimaryKeys() {
        return primaryKeys;
    }

    public void setPrimaryKeys(List<ColumnInfo> primaryKeys) {
        this.primaryKeys = primaryKeys;
    }
}
