package com.serendipity.rpc.proxy.api;

import com.serendipity.rpc.proxy.api.config.ProxyConfig;
import com.serendipity.rpc.spi.annotation.SPI;

/**
 * 代理工厂接口
 *
 * @author serendipity
 * @version 1.0
 * @date 2024/2/18
 **/
@SPI
public interface ProxyFactory {

    /**
     * 获取代理对象
     */
    <T> T getProxy(Class<T> clazz);

    /**
     * 默认初始化方法
     */
    default <T> void init(ProxyConfig<T> proxyConfig) {}
}
