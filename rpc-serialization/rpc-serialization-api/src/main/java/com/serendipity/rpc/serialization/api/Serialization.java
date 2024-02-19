package com.serendipity.rpc.serialization.api;

import com.serendipity.rpc.constants.RpcConstants;
import com.serendipity.rpc.spi.annotation.SPI;

/**
 * 序列化接口 , 当未指定序列化方式时，默认选择 括号中的SERIALIZATION_JDK序列化方式
 *
 * @author serendipity
 * @version 1.0
 * @date 2024/2/5
 **/
@SPI(RpcConstants.SERIALIZATION_JDK)
public interface Serialization {
    /**
     * 序列化
     */
    <T> byte[] serialize(T obj);

    /**
     * 反序列化
     */
    <T> T deserialize(byte[] data, Class<T> cls);
}
