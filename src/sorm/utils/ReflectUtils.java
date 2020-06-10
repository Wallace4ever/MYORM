package sorm.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 封装常用的反射操作
 * @author wallace
 */
public class ReflectUtils {
    /**
     * 调用object对象的属性fieldName对应的get方法
     * @param fieldName 属性名
     * @param object 对象
     */
    public static Object invokeGet(String fieldName, Object object) {
        //通过反射机制调用该对象该属性对应的get方法
        Object obj=null;
        try {
            Class cla=object.getClass();
            Method method=cla.getDeclaredMethod("get"+ StringUtil.firstChar2UpperCase(fieldName),null);
            obj=method.invoke(object,null);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return obj;
    }

    public static void invokeSet(Object obj,String columnName,Object columnValue) {
        Method m= null;
        try {
            m = obj.getClass().getDeclaredMethod("set"+ StringUtil.firstChar2UpperCase(columnName),columnValue.getClass());
            m.invoke(obj,columnValue);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
