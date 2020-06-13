package sorm.core;

import sorm.bean.ColumnInfo;
import sorm.bean.TableInfo;
import sorm.utils.JDBCUtils;
import sorm.utils.ReflectUtils;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 负责对外查询的核心类
 * @author wallace
 */
@SuppressWarnings("all")
public abstract class Query implements Cloneable{
    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    /**
     * 采用模版方法模式将JDBC操作封装成模版，便于重用
     * @param sql sql语句
     * @param params sql的参数
     * @param cla 要封装到的java类
     * @param callback Callback的实现类，实现回调
     * @return 结果
     */
    public Object executeQueryTemplate(String sql,Object[] params,Class cla,Callback callback) {
        Connection conn=DBManager.getConn();
        ResultSet rs=null;
        PreparedStatement ps=null;
        try {
            ps=conn.prepareStatement(sql);
            JDBCUtils.handleParams(ps,params);
            //System.out.println(ps);
            rs=ps.executeQuery();
            Object result=callback.doExecute(conn,ps,rs);
            DBManager.close(rs,ps,conn);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 直接执行一个DML语句
     * @param sql sql语句
     * @param params 参数
     * @return 执行语句后影响的行数
     */
    public int executeDML(String sql, Object[] params) {
        Connection conn=DBManager.getConn();
        int count =0;
        PreparedStatement ps=null;
        try {
            ps=conn.prepareStatement(sql);
            JDBCUtils.handleParams(ps,params);
            System.out.println(ps);
            count=ps.executeUpdate();
            DBManager.close(ps,conn);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return count;
    }

    /**
     * 将一个不为空的对象存储到数据库中，如果数字为null则放0
     * @param object 要存储的对象
     */
    public void insert(Object object) {
        //obj-> insert into 表名 (字段名,字段名，……) values (参数,?,...)
        Class cla=object.getClass();
        TableInfo tableInfo=TableContext.poClassTableMap.get(cla);
        List<Object> list=new ArrayList<>();//存储sql参数对象
        StringBuilder sql=new StringBuilder("insert into "+tableInfo.getT_name()+" (");

        //获得类所有的属性，以及将参数对象插入list中
        Field[] fs=cla.getDeclaredFields();
        int countNotNullField=0;
        for (Field f : fs) {
            String fieldName=f.getName();
            Object fieldValue= ReflectUtils.invokeGet(fieldName,object);
            if (object != null) {
                sql.append(fieldName+",");
                countNotNullField++;
                list.add(fieldValue);
            }
        }
        sql.setCharAt(sql.length()-1,')');
        sql.append(" values (");
        for (int i = 0; i < countNotNullField; i++) {
            sql.append("?,");
        }
        sql.setCharAt(sql.length()-1,')');
        executeDML(sql.toString(),list.toArray());
    }

    /**
     * 删除cla表示类对应的表中指定id的记录
     * @param cla 与表对应的类对象
     * @param id 主键的值
     * @return 执行sql后影响的行数
     */
    public int delete(Class cla, Object id) {
        //Emp,2-> delete from emp where id=2
        //通过Class对象找TableInfo，进一步得到表名
        TableInfo tableInfo=TableContext.poClassTableMap.get(cla);
        ColumnInfo onlyPriKey=tableInfo.getPrimaryKey();
        String sql="delete from "+tableInfo.getT_name()+" where "+onlyPriKey.getName()+"=?";
        return  executeDML(sql,new Object[]{id});
    }

    /**
     * 删除对象在数据库中对应的记录（对象所属的类对应到表，对象主键的值对应到记录）
     * @param object 要删除的记录所对应的对象
     */
    public void delete(Object object) {
        Class c=object.getClass();
        TableInfo tableInfo=TableContext.poClassTableMap.get(c);
        ColumnInfo primaryKey=tableInfo.getPrimaryKey();
        //通过反射机制调用属性对应的get/set方法
        Object value=ReflectUtils.invokeGet(primaryKey.getName(),object);
        delete(c,value);
    }

    /**
     * 更新对象对应的记录，并且只更新指定的字段的值
     * @param object 要更新的字段对应的对象
     * @param filedNames 要更新的属性列表
     * @return 执行sql后影响的行数
     */
    public int update(Object object, String[] filedNames) {
        //obj{"uname","pwd"}-> update 表名 set uname=?,pwd=? where id=?
        Class cla=object.getClass();
        TableInfo tableInfo=TableContext.poClassTableMap.get(cla);
        ColumnInfo primaryKey=tableInfo.getPrimaryKey();
        List<Object> list=new ArrayList<>();
        StringBuilder sql =new StringBuilder("update "+tableInfo.getT_name()+" set ");

        for (String fname : filedNames) {
            Object fvalue=ReflectUtils.invokeGet(fname,object);
            list.add(fvalue);
            sql.append(fname+"=?,");
        }
        list.add(ReflectUtils.invokeGet(primaryKey.getName(),object));
        sql.setCharAt(sql.length()-1,' ');
        sql.append("where ");
        sql.append(primaryKey.getName()+"=? ");
        return executeDML(sql.toString(),list.toArray());
    }

    /**
     * 查询返回多行记录，并将每行记录封装到cla指定类的对象中
     * @param sql 查询语句
     * @param cla 封装数据的Javabean类的类对象
     * @param params sql的参数
     * @return 查询到的结果
     */
    public List queryRows(final String sql, final Class cla, final Object[] params) {
        //调用上面的模版方法，通过回调来执行更加灵活和复杂的查询
        return (List) executeQueryTemplate(sql, params, cla, new Callback() {
            @Override
            //直接拿到conn,ps,rs进一步处理
            public Object doExecute(Connection conn, PreparedStatement ps, ResultSet rs) {
                ResultSetMetaData metaData= null;
                List list=null;
                try {
                    if (list == null) {
                        list=new ArrayList();
                    }
                    metaData = rs.getMetaData();
                    while (rs.next()) {
                        //调用Javabean的无参构造器
                        Object rowObj=cla.newInstance();
                        //多列query如 select username,pwd,age from user where id>? and age >18
                        for (int i = 0; i<metaData.getColumnCount();i++) {
                            String columnName=metaData.getColumnLabel(i+1);
                            Object columnValue=rs.getObject(i+1);

                            //调用rowObject的setUsername方法，将columnValue放进去
                            ReflectUtils.invokeSet(rowObj,columnName,columnValue);
                        }
                        list.add(rowObj);
                    }
                } catch (SQLException | InstantiationException | IllegalAccessException throwables) {
                    throwables.printStackTrace();
                }
                return list;
            }
        });
    }

    /**
     * 查询返回一行记录，并将每行记录封装到cla指定类的对象中
     * @param sql 查询语句
     * @param cla 封装数据的Javabean类的类对象
     * @param params sql的参数
     * @return 查询到的结果
     */
    public Object queryUniqueRow(String sql, Class cla, Object[] params) {
        List list=queryRows(sql, cla, params);
        return (list!=null&&list.size()>0)?list.get(0):null;
    }

    /**
     * 根据主键值直接查找对应的对象
     * @param cla 类
     * @param id 主键
     * @return 对应的对象
     */
    public Object queryById(Class cla, Object id) {
        //select * from emp where id=?
        TableInfo tableInfo=TableContext.poClassTableMap.get(cla);
        ColumnInfo onlyPriKey=tableInfo.getPrimaryKey();
        String sql="select * from "+tableInfo.getT_name()+" where "+onlyPriKey.getName()+"=?";
        return queryUniqueRow(sql,cla,new Object[]{id});
    }

    /**
     * 查询单个值，这时就不用封装到Javabean对象中了
     * @param sql 查询语句
     * @param params sql的参数
     * @return 查询到的单个值的封装类
     */
    public Object queryValue(String sql, Object[] params) {
        return executeQueryTemplate(sql, params, null, new Callback() {
            @Override
            public Object doExecute(Connection conn, PreparedStatement ps, ResultSet rs) {
                Object value=null;
                try {
                    while (rs.next()) {
                        value = rs.getObject(1);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return value;
            }
        });
    }

    /**
     * 查询单个数字，可以是Integer/Double/Long等等
     * @param sql 查询语句
     * @param params sql的参数
     * @return 查询到的数字封装类
     */
    public Number queryNumber(String sql, Object[] params) {
        return (Number) queryValue(sql, params);
    }

    /**
     * 分页查询，不同数据库具体不一样
     * @param pageNum 第几页数据
     * @param size 每页显示多少条记录
     * @return 每页查询到的所有结果
     */
    public abstract Object queryPage(int pageNum,int size);
}
