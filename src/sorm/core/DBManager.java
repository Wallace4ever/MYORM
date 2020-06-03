package sorm.core;

import sorm.Main;
import sorm.bean.Configuration;

import java.io.Closeable;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * 根据配置信息，维持连接对象的管理（增加连接池的功能）
 */
public class DBManager {
    private static Configuration conf=null;
    static {//只需要加载一次配置资源文件
        Properties properties=new Properties();
        try {
            properties.load(Main.class.getResourceAsStream("db.properties"));
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
    }

    public static Connection getConn() {
        try {
            Class.forName(conf.getDriver());
            //直接建立连接，后面增加连接池处理
            return DriverManager.getConnection(conf.getUrl(),conf.getUser(),conf.getPwd());
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void close(Closeable ... set) {
        for (Closeable element : set) {
            try {
                element.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获得Configuration对象
     * @return Configuration对象
     */
    public static Configuration getConf() {
        return conf;
    }
}
