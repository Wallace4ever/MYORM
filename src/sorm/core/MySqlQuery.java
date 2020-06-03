package sorm.core;

import sorm.bean.ColumnInfo;
import sorm.bean.TableInfo;
import sorm.utils.ReflectUtils;
import sorm.utils.StringUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * 负责对MySQL数据库查询的核心类
 * @author wallace
 */
public class MySqlQuery implements Query{
    @Override
    public int executeDML(String sql, Object[] params) {
        return 0;
    }

    @Override
    public void insert(Object object) {

    }

    @Override
    public int delete(Class cla, Object id) {
        //Emp.class,2-> delete from emp where id=2
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
        return 0;
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
