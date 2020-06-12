package sorm.core;

import sorm.bean.ColumnInfo;
import sorm.bean.TableInfo;
import sorm.utils.JavaFileUtils;
import sorm.utils.StringUtil;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 负责管理数据库所有表结构和类结构的关系，并可以根据表结构生成类结构
 * @author wallace
 */
public class TableContext {
    /**
     * 表名为key，表信息对象为value
     */
    public static Map<String, TableInfo> tables = new HashMap<>();
    /**
     * 将Class对象和表信息对象关联起来，便于重用
     */
    public static Map<Class, TableInfo> poClassTableMap = new HashMap<>();

    private TableContext() {
    }

    static {
        //初始化获得数据库整个表结构信息加载进来
        Connection conn= DBManager.getConn();
        try {
            DatabaseMetaData metaData=conn.getMetaData();
            ResultSet tableSet=metaData.getTables("orm","%","%",new String[]{"TABLE"});
            while (tableSet.next()) {
                String tableName=(String) tableSet.getObject("TABLE_NAME");
                //System.out.println(tableName);
                TableInfo tableInfo=new TableInfo(tableName, new HashMap<>(),new ArrayList<>());
                tables.put(tableName,tableInfo);//表信息和表明关联在一起放到了类变量tables容器中

                ResultSet set= metaData.getColumns(null,"%",tableName,"%");//查询表中的所有字段,填充到表信息中
                while (set.next()) {
                    ColumnInfo columnInfo=new ColumnInfo(set.getString("COLUMN_NAME"),set.getString("TYPE_NAME"),0);
                    tableInfo.getColumns().put(set.getString("COLUMN_NAME"),columnInfo);
                }

                ResultSet set2=metaData.getPrimaryKeys(null,"%",tableName);//查询t_user表中的主键
                while (set2.next()) {
                    ColumnInfo columnInfo=(ColumnInfo) tableInfo.getColumns().get(set2.getObject("COLUMN_NAME"));
                    columnInfo.setKeyType(1);
                    tableInfo.getPrimaryKeys().add(columnInfo);//把所有主键放到表信息的主键容器中
                }

                if (tableInfo.getPrimaryKeys().size() > 0) {//把获取到的第一个主键的引用存储到唯一主键
                    tableInfo.setPrimaryKey(tableInfo.getPrimaryKeys().get(0));
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        //更新类结构
        updateJavaPOFile();
        //加载po包下面所有的类，便于重用，提高效率
        loadPOTables();
    }

    public static Map<String, TableInfo> getTableInfos() {
        return tables;
    }

    /**
     * 根据表结构，更新配置的po包下面的java类
     * 实现了从表结构转换为类结构
     */
    public static void updateJavaPOFile() {
        Map<String,TableInfo> map=tables;
        for (TableInfo ti : map.values()) {
            JavaFileUtils.createJavaPOFile(ti,new MySqlTypeConverter());
        }
    }

    /**
     * 加载所有表对应的po包中的类（java源文件已存在），并把这些类和表放入Map中
     */
    public static void loadPOTables() {
        for (TableInfo tableInfo : tables.values()) {
            try {
                Class c= Class.forName(DBManager.getConf().getPoPackage()
                        +"."+ StringUtil.firstChar2UpperCase(tableInfo.getT_name()));
                poClassTableMap.put(c,tableInfo);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
