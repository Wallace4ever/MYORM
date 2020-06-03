package sorm.bean;

/**
 * 管理配置信息
 * @author wallace
 */
public class Configuration {
    /**
     * 驱动类
     */
    private String driver;
    /**
     *数据库URL
     */
    private String url;
    /**
     *数据库用户名
     */
    private String user;
    /**
     *数据库密码
     */
    private String pwd;
    /**
     *正在使用哪一个数据库
     */
    private String currentDB;
    /**
     *项目的源码路径
     */
    private String srcPath;
    /**
     *扫描生成Java类的包，po指的是Persistence Object持久化对象
     */
    private String poPackage;

    public Configuration() {
    }

    public Configuration(String driver, String url, String user, String pwd, String currentDB, String srcPath, String poPackage) {
        this.driver = driver;
        this.url = url;
        this.user = user;
        this.pwd = pwd;
        this.currentDB = currentDB;
        this.srcPath = srcPath;
        this.poPackage = poPackage;
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getCurrentDB() {
        return currentDB;
    }

    public void setCurrentDB(String currentDB) {
        this.currentDB = currentDB;
    }

    public String getSrcPath() {
        return srcPath;
    }

    public void setSrcPath(String srcPath) {
        this.srcPath = srcPath;
    }

    public String getPoPackage() {
        return poPackage;
    }

    public void setPoPackage(String poPackage) {
        this.poPackage = poPackage;
    }
}
