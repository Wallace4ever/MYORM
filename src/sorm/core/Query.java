package sorm.core;

import java.util.List;

/**
 * 负责对外查询的核心类
 * @author wallace
 */
@SuppressWarnings("all")
public interface Query {
    /**
     * 直接执行一个DML语句
     * @param sql sql语句
     * @param params 参数
     * @return 执行语句后影响的行数
     */
    public int executeDML(String sql, Object[] params);

    /**
     * 将一个不为空的对象存储到数据库中，如果数字为null则放0
     * @param object 要存储的对象
     */
    public void insert(Object object);

    /**
     * 删除cla表示类对应的表中指定id的记录
     * @param cla 与表对应的类对象
     * @param id 主键的值
     * @return 执行sql后影响的行数
     */
    public int delete(Class cla, Object id);//delete from user where id =2;

    /**
     * 删除对象在数据库中对应的记录（对象所属的类对应到表，对象主键的值对应到记录）
     * @param object 要删除的记录所对应的对象
     */
    public void delete(Object object);

    /**
     * 更新对象对应的记录，并且只更新指定的字段的值
     * @param object 要更新的字段对应的对象
     * @param filedNames 要更新的属性列表
     * @return 执行sql后影响的行数
     */
    public int update(Object object, String[] filedNames);//update user set uname=?,pwd=? where id=?;

    /**
     * 查询返回多行记录，并将每行记录封装到cla指定类的对象中
     * @param sql 查询语句
     * @param cla 封装数据的Javabean类的类对象
     * @param params sql的参数
     * @return 查询到的结果
     */
    public List queryRows(String sql, Class cla, Object[] params);

    /**
     * 查询返回一行记录，并将每行记录封装到cla指定类的对象中
     * @param sql 查询语句
     * @param cla 封装数据的Javabean类的类对象
     * @param params sql的参数
     * @return 查询到的结果
     */
    public Object queryUniqueRow(String sql,Class cla, Object[] params);

    /**
     * 查询单个值，这时就不用封装到Javabean对象中了
     * @param sql 查询语句
     * @param params sql的参数
     * @return 查询到的单个值的封装类
     */
    public Object queryValue(String sql, Object[] params);

    /**
     * 查询单个数字，可以是Integer/Double/Long等等
     * @param sql 查询语句
     * @param params sql的参数
     * @return 查询到的数字封装类
     */
    public Number queryNumber(String sql, Object[] params);
}
