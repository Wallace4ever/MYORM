package sorm.core;

import sorm.bean.Configuration;
import sorm.pool.DBConnPool;

import java.io.IOException;
import java.sql.*;
import java.util.Properties;

/**
 * 根据配置信息，维持连接对象的管理（增加连接池的功能）
 */
public class DBManager {
    /**
     * 配置信息
     */
    private static Configuration conf=null;
    /**
     * 连接池对象
     */
    private static DBConnPool pool;

    static {//只需要加载一次配置资源文件
        Properties properties=new Properties();
        try {
            //properties.load(DBManager.class.getResourceAsStream("/sorm/db.properties"));
            properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("sorm/db.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        conf=new Configuration();
        conf.setDriver(properties.getProperty("driver"));
        conf.setPoPackage(properties.getProperty("poPackage"));
        conf.setPwd(properties.getProperty("pwd"));
        conf.setSrcPath(properties.getProperty("srcPath"));
        conf.setUrl(properties.getProperty("url"));
        conf.setUser(properties.getProperty("user"));
        conf.setCurrentDB(properties.getProperty("currentDB"));
        conf.setQueryClass(properties.getProperty("queryClass"));
        conf.setPoolMaxSize(Integer.parseInt(properties.getProperty("poolMaxSize")));
        conf.setPoolMinSize(Integer.parseInt(properties.getProperty("poolMinSize")));

        //加载TableContext
        try {
            Class.forName("sorm.core.TableContext");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 从连接池中获得Connection对象
     * @return Connection对象
     */
    public static Connection getConn() {
        if (pool == null) {
            pool=new DBConnPool();
        }
        return pool.getConnection();
    }

    /**
     * 获得Connection对象
     * @return Connection对象
     */
    public static Connection createConn() {
        try {
            Class.forName(conf.getDriver());
            //直接建立连接，后面增加连接池处理
            return DriverManager.getConnection(conf.getUrl(),conf.getUser(),conf.getPwd());
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 关闭传入的ResultSet、Statement和Connection对象
     * @param rs 结果集
     * @param ps Statement对象
     * @param conn 连接对象
     */
    public static void close(ResultSet rs, Statement ps,Connection conn) {
        try {
            if (rs != null)
                rs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (ps != null)
                ps.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (conn != null)
                //conn.close();//真关闭
                pool.close(conn);//假关闭
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 关闭传入的Statement和Connection对象
     * @param ps Statement对象
     * @param conn 连接对象
     */
    public static void close(Statement ps,Connection conn) {
        try {
            if (ps != null)
                ps.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (conn != null)
                //conn.close();//真关闭
                pool.close(conn);//假关闭
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 单独关闭连接
     * @param conn 连接对象
     */
    public static void close(Connection conn) {
        if (conn!=null)
            pool.close(conn);
    }

    /**
     * 获得Configuration对象
     * @return Configuration对象
     */
    public static Configuration getConf() {
        return conf;
    }
}
