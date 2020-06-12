package sorm.core;

/**
 * 创建Query对象的工厂类，工厂本身用了单例模式，创建时使用了克隆模式。
 */
public class QueryFactory {
    private static Query prototypeObj;//原型对象
    Class c;
    private QueryFactory() {}

    static {

        try {
            //加载指定的Query类
            Class c= Class.forName(DBManager.getConf().getQueryClass());
            prototypeObj=(Query) c.newInstance();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static Query createQuery(){
        try {
            return (Query) prototypeObj.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return null;
        }
    }
}

/*每次使用反射创建实例，效率较低
class QueryFactory{
    private static QueryFactory factory=new QueryFactory();
    //私有构造方法，单例模式
    private QueryFactory() {}
    Class c;
    static {
        try {
            //加载指定的Query类
            Class c= Class.forName(DBManager.getConf().getQueryClass());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    public Query createQuery(){
        try {
            return (Query)c.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static QueryFactory getFactory(){
        return factory;
    }
}*/
