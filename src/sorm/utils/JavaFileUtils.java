package sorm.utils;

import sorm.bean.ColumnInfo;
import sorm.bean.JavaFieldGetSet;
import sorm.bean.TableInfo;
import sorm.core.DBManager;
import sorm.core.MySqlTypeConverter;
import sorm.core.TableContext;
import sorm.core.TypeConverter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 封装了生成Java文件（源代码）常用的操作
 * @author wallace
 */
public class JavaFileUtils {
    /**
     * 根据字段生成Java属性信息以及相应的get/set源码。如varchar username-> private String username;
     * @param column 字段信息
     * @param converter 类型转化器
     * @return Java属性以及相应的get/set源码
     */
    public static JavaFieldGetSet createFieldGetSetSRC(ColumnInfo column, TypeConverter converter) {
        JavaFieldGetSet jfgs=new JavaFieldGetSet();
        String javaFieldType = converter.db2JavaType(column.getDataType());
        jfgs.setFieldInfo("\tprivate "+ javaFieldType+" "+column.getName()+";\n");

        //要生成get方法源码：类似于public String getUsername(){return username;}这样的代码
        StringBuilder getSrc=new StringBuilder();
        getSrc.append("\tpublic "+javaFieldType+" get"+StringUtil.firstChar2UpperCase(column.getName())+"(){\n");
        getSrc.append("\t\treturn "+column.getName()+";\n");
        getSrc.append("\t}\n");
        jfgs.setGetInfo(getSrc.toString());

        //要生成set方法源码：类似于public void setUsername(String username){this.username=username;}这样的代码
        StringBuilder setSrc=new StringBuilder();
        setSrc.append("\tpublic void"+" set"+StringUtil.firstChar2UpperCase(column.getName())+"(");
        setSrc.append(javaFieldType+" "+column.getName()+"){\n");
        setSrc.append("\t\tthis."+column.getName()+"="+column.getName()+";\n");
        setSrc.append("\t}\n");
        jfgs.setSetInfo(setSrc.toString());

        return jfgs;
    }

    /**
     * 根据表信息生成java类的源代码
     * @param tableInfo 表信息
     * @param converter 数据类型转换器
     * @return java类的源码
     */
    public static String createJavaSrc(TableInfo tableInfo,TypeConverter converter) {
        Map<String,ColumnInfo> columns=tableInfo.getColumns();
        List<JavaFieldGetSet> javaFields=new ArrayList<>();
        for (ColumnInfo c : columns.values()) {
            javaFields.add(createFieldGetSetSRC(c,converter));
        }

        StringBuilder src=new StringBuilder();
        //生成package语句
        src.append("package "+ DBManager.getConf().getPoPackage()+";\n\n");
        //生成import语句
        src.append("import java.sql.*;\n");
        src.append("import java.util.*;\n\n");
        //生成类声明语句
        src.append("public class "+StringUtil.firstChar2UpperCase(tableInfo.getT_name())+" {\n\n");
        //生成属性列表
        for (JavaFieldGetSet f : javaFields) {
            src.append(f.getFieldInfo());
        }
        src.append("\n\n");
        //生成get方法列表
        for (JavaFieldGetSet f : javaFields) {
            src.append(f.getGetInfo());
        }
        //生成set方法列表
        for (JavaFieldGetSet f : javaFields) {
            src.append(f.getSetInfo());
        }
        //生成类结束符
        src.append("}\n");
        //System.out.println(src);
        return src.toString();
    }

    public static void createJavaPOFile(TableInfo tableInfo, TypeConverter converter) {
        String src=createJavaSrc(tableInfo,converter);
        String srcPath=DBManager.getConf().getSrcPath()+"/";
        String packagePath=DBManager.getConf().getPoPackage().replaceAll("\\.","/");

        File file=new File(srcPath+packagePath);
        //System.out.println(file.getAbsolutePath());
        if (!file.exists()) {//指定目录不存在则帮助用户建立
            file.mkdirs();
        }

        BufferedWriter bw=null;
        try {
            bw=new BufferedWriter(new FileWriter(file.getAbsolutePath()+"/"+StringUtil.firstChar2UpperCase(tableInfo.getT_name())+".java"));
            bw.write(src);
            bw.flush();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*public static void main(String[] args) {
        ColumnInfo ci=new ColumnInfo("age","smallint",0);
        JavaFieldGetSet f=createFieldGetSetSRC(ci,new MySqlTypeConverter());
        System.out.println(f);
    }*/
}
