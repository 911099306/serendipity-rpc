package com.serendipity.rpc.common.ip;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;

/**
 * IP工具类
 *
 * @author serendipity
 * @version 1.0
 * @date 2024/2/20
 **/
public class IpUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(IpUtils.class);

    public static InetAddress getLocalInetAddress()  {
        try{
            return InetAddress.getLocalHost();
        }catch (Exception e){
            LOGGER.error("get local ip address throws exception: {}", e);
        }
        return null;
    }

    public static String getLocalAddress(){
        return getLocalInetAddress().toString();
    }

    public static String getLocalHostName(){
        return getLocalInetAddress().getHostName();
    }

    public static String getLocalHostIp(){
        return getLocalInetAddress().getHostAddress();
    }
}
