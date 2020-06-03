package sorm.utils;

/**
 * 封装常用的字符串操作
 * @author wallace
 */
public class StringUtil {
    /**
     * 将目标字符串首字母变为大写
     * @param str 目标字符串
     * @return 首字母变为大写的字符串
     */
    public static String firstChar2UpperCase(String str) {
        return str.toUpperCase().substring(0,1)+str.substring(1);
    }
}
