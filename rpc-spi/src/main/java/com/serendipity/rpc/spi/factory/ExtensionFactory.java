package com.serendipity.rpc.spi.factory;

import com.serendipity.rpc.spi.annotation.SPI;

/**
 * 扩展类加载器的工程接口
 * @author serendipity
 * @version 1.0
 * @date 2024/2/19
 **/
@SPI("spi")
public interface ExtensionFactory {

    /**
     * 获取扩展类对象
     * @param <T> 泛型类型
     * @param key 传入的 key 值
     * @param clazz Class 类型对象
     * @return 扩展类对象
     */
    <T> T getExtension(String key, Class<T> clazz);
}
