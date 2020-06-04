package sorm.core;

import sorm.bean.ColumnInfo;
import sorm.bean.TableInfo;
import sorm.utils.JDBCUtils;
import sorm.utils.ReflectUtils;


import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 负责对MySQL数据库查询的核心类
 * @author wallace
 */
public class MySqlQuery implements Query{
/*    public static void main(String[] args) {
        Dept dept=new Dept();
        dept.setId(1);
        dept.setAddress("大上海");
        dept.setDname("法务部");
        new MySqlQuery().update(dept,new String[]{"address","dname"});
    }*/
    @Override
    public int executeDML(String sql, Object[] params) {
        Connection conn=DBManager.getConn();
        int count =0;
        PreparedStatement ps=null;
        try {
            ps=conn.prepareStatement(sql);
            JDBCUtils.handleParams(ps,params);
            System.out.println(ps);
            count=ps.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return 0;
    }

    @Override
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
            Object fieldValue=ReflectUtils.invokeGet(fieldName,object);
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

    @Override
    public int delete(Class cla, Object id) {
        //Emp,2-> delete from emp where id=2
        //通过Class对象找TableInfo，进一步得到表名
        TableInfo tableInfo=TableContext.poClassTableMap.get(cla);
        ColumnInfo onlyPriKey=tableInfo.getPrimaryKey();
        String sql="delete from "+tableInfo.getT_name()+" where "+onlyPriKey.getName()+"=?";
        return  executeDML(sql,new Object[]{id});
    }

    @Override
    public void delete(Object object) {
        Class c=object.getClass();
        TableInfo tableInfo=TableContext.poClassTableMap.get(c);
        ColumnInfo primaryKey=tableInfo.getPrimaryKey();
        //通过反射机制调用属性对应的get/set方法
        Object value=ReflectUtils.invokeGet(primaryKey.getName(),object);
        delete(c,value);
    }

    @Override
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

    @Override
    public List queryRows(String sql, Class cla, Object[] params) {
        return null;
    }

    @Override
    public Object queryUniqueRow(String sql, Class cla, Object[] params) {
        return null;
    }

    @Override
    public Object queryValue(String sql, Object[] params) {
        return null;
    }

    @Override
    public Number queryNumber(String sql, Object[] params) {
        return null;
    }
}
