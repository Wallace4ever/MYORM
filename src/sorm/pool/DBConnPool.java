package sorm.pool;

import sorm.core.DBManager;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 连接池类
 */
public class DBConnPool {
    /**
     * 连接池对象
     */
    private List<Connection> pool;
    /**
     * 最大链接数
     */
    private static final int POOL_MAX_SIZE = DBManager.getConf().getPoolMaxSize();
    /**
     * 最小连接数
     */
    private static final int POOL_MIN_SIZE = DBManager.getConf().getPoolMinSize();

    public DBConnPool() {
        initPool();
    }

    /**
     * 初始化连接池，使连接数量达到最小值
     */
    public void initPool() {
        if (pool == null) {
            pool=new ArrayList<>();
        }
        while (pool.size() < DBConnPool.POOL_MIN_SIZE) {
            pool.add(DBManager.createConn());
        }
        //System.out.println("初始化池，连接数为："+pool.size());
    }

    /**
     * 从连接池中取出一个连接
     * @return 取出的连接
     */
    public synchronized Connection getConnection() {
        int lastIndex=pool.size()-1;
        Connection conn=pool.get(lastIndex);
        pool.remove(lastIndex);
        return conn;
    }

    /**
     * 并不是真正关闭，将连接放回池中
     * @param conn 要放回的连接
     */
    public synchronized void close(Connection conn) {
        if (pool.size() >= POOL_MAX_SIZE) {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            pool.add(conn);
        }
    }
}
