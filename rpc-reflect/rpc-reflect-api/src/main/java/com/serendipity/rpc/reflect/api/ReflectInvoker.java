package com.serendipity.rpc.reflect.api;

import com.serendipity.rpc.spi.annotation.SPI;

/**
 * 反射方法的调用接口
 *
 * @author serendipity
 * @version 1.0
 * @date 2024/2/20
 **/
@SPI
public interface ReflectInvoker {

    /**
     * 调用真实方法的 SPI 通用接口
     * @param serviceBean 方法所在的对象实例
     * @param serviceClass 方法所在对象实例的 Class 对象
     * @param methodName 方法名称
     * @param parameterTypes 方法的参数类型数组
     * @param parameters 方法的参数数组
     * @return 方法调用的结果信息
     * @throws Throwable 抛出的异常
     */
    Object invokeMethod(Object serviceBean, Class<?> serviceClass, String methodName, Class<?>[] parameterTypes, Object[] parameters) throws Throwable;

}
