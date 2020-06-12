package sorm.core;

import po.Dept;
import po.Emp;
import sorm.bean.ColumnInfo;
import sorm.bean.TableInfo;
import sorm.utils.JDBCUtils;
import sorm.utils.ReflectUtils;


import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 负责对MySQL数据库查询的核心类
 * @author wallace
 */
public class MySqlQuery extends Query {
/*    public static void main(String[] args) {
        //测试update
        Dept dept=new Dept();
        dept.setId(1);
        dept.setAddress("大上海");
        dept.setDname("法务部");
        new MySqlQuery().update(dept,new String[]{"address","dname"});

        //测试query
        List<Emp> list = new MySqlQuery().queryRows("select id,empname,age,deptId from emp where age>? and salary>?",
                Emp.class, new Object[]{18, 5000});
        for (Emp e : list) {
            System.out.println(e.getEmpname());
        }

        //测试query单个值，使用Number便于在使用时转型
        Object obj=new MySqlQuery().queryValue("select count(*) from emp where salary>?",new Object[]{5000});
        Number obj2=new MySqlQuery().queryNumber("select count(*) from emp where salary>=?",new Object[]{5000});
        System.out.println(obj2.doubleValue());
    }*/

    @Override
    public Object queryPage(int pageNum, int size) {
        return null;
    }
}
