package sorm.bean;

/**
 * 封装了创建表对应的Java类的源文件中，单个属性的声明和get、set方法的源代码
 */
public class JavaFieldGetSet {
    /**
     * 属性的源码，如：private int userId
     */
    private String fieldInfo;
    /**
     * get方法的源码，如：public int getUserId(){}
     */
    private String getInfo;
    /**
     * set方法的源码，如：public void setUserId(int id){}
     */
    private String setInfo;

    public JavaFieldGetSet() {
    }

    public JavaFieldGetSet(String fieldInfo, String getInfo, String setInfo) {
        this.fieldInfo = fieldInfo;
        this.getInfo = getInfo;
        this.setInfo = setInfo;
    }

    public String getFieldInfo() {
        return fieldInfo;
    }

    public void setFieldInfo(String fieldInfo) {
        this.fieldInfo = fieldInfo;
    }

    public String getGetInfo() {
        return getInfo;
    }

    public void setGetInfo(String getInfo) {
        this.getInfo = getInfo;
    }

    public String getSetInfo() {
        return setInfo;
    }

    public void setSetInfo(String setInfo) {
        this.setInfo = setInfo;
    }

/*    @Override
    public String toString() {
        System.out.println(fieldInfo);
        System.out.println(getInfo);
        System.out.println(setInfo);
        return super.toString();
    }*/
}
