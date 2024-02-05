package com.serendipity.rpc.serialization.api;

/**
 * 序列化接口
 *
 * @author serendipity
 * @version 1.0
 * @date 2024/2/5
 **/
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
