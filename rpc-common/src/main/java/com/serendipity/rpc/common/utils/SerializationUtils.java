package com.serendipity.rpc.common.utils;

import java.util.stream.IntStream;

/**
 * 序列化时针对消息头序列话类型的操作
 *
 * @author serendipity
 * @version 1.0
 * @date 2024/2/5
 **/
public class SerializationUtils {

    private static final String PADDING_STRING = "0";

    /**
     * 约定序列化类型的最大长度为 16
     */
    public static final int MAX_SERIALIZATION_TYPE_COUNR = 16;


    /**
     * 长度不足16 后面补0
     * @param str 原始字符串
     * @return 满足长度为16的字符串
     */
    public static String paddingString(String str) {
        // 将 null => ""
        str = transNullToEmpty(str);
        // 最大长度不可超过16
        if (str.length() >= MAX_SERIALIZATION_TYPE_COUNR) return str;
        // 需要补充 0 的数量
        int paddingCount = MAX_SERIALIZATION_TYPE_COUNR - str.length();
        StringBuilder paddingString = new StringBuilder(str);
        IntStream.range(0, paddingCount).forEach((i) -> {
            paddingString.append(PADDING_STRING);
        });
        return paddingString.toString();
    }


    /**
     * 字符串去0 操作
     * @param str 原始字符串
     * @return 去0后的字符串
     */
    public static String subString(String str){
        str = transNullToEmpty(str);
        return str.replace(PADDING_STRING, "");
    }

    /**
     *  将 null => ""
     * @param str 原始字符串
     * @return 不会为nul，至少是 “”
     */
    public static String transNullToEmpty(String str){
        return str == null ? "" : str;
    }


}
