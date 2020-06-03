package sorm.core;

/**
 * 负责java数据类型和数据库数据类型的互相转换
 * @author wallace
 */
public interface TypeConverter {
    /**
     * 将数据库数据类型转化成Java数据类型
     * @param columnType 数据库字段的数据类型
     * @return Java的数据类型
     */
    public String db2JavaType(String columnType);

    /**
     * 将Java数据类型转化成数据库数据类型
     * @param javaType Java的数据类型
     * @return 数据库字段的数据类型
     */
    public String java2DbType(String javaType);
}
