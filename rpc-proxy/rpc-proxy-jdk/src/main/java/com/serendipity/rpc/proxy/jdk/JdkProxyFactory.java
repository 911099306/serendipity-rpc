package com.serendipity.rpc.proxy.jdk;

import com.serendipity.rpc.proxy.api.BaseProxyFactory;
import com.serendipity.rpc.proxy.api.ProxyFactory;
import com.serendipity.rpc.proxy.api.consumer.Consumer;
import com.serendipity.rpc.proxy.api.object.ObjectProxy;

import java.lang.reflect.Proxy;

/**
 * @author serendipity
 * @version 1.0
 * @date 2024/2/18
 **/
public class JdkProxyFactory<T> extends BaseProxyFactory<T> implements ProxyFactory {

    @Override
    public <T> T getProxy(Class<T> clazz) {
        return (T) Proxy.newProxyInstance(
                clazz.getClassLoader(),
                new Class<?>[]{clazz},
                objectProxy
        );
    }
}
