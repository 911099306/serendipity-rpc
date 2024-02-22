package com.serendipity.rpc.common.utils;

/**
 * 字符串工具类
 * @author serendipity
 * @version 1.0
 * @date 2024/2/23
 **/
public class StringUtils {

    public static boolean isEmpty(String str){
        return str == null || str.trim().isEmpty();
    }
}
