package com.serendipity.rpc.proxy.cglib;

import com.serendipity.rpc.proxy.api.BaseProxyFactory;
import com.serendipity.rpc.proxy.api.ProxyFactory;
import com.serendipity.rpc.spi.annotation.SPIClass;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.InvocationHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

/**
 * @author serendipity
 * @version 1.0
 * @date 2024/2/20
 **/
@SPIClass
public class CglibProxyFactory<T> extends BaseProxyFactory<T> implements ProxyFactory {
    private final Logger logger = LoggerFactory.getLogger(CglibProxyFactory.class);
    private final Enhancer enhancer = new Enhancer();
    @Override
    public <T> T getProxy(Class<T> clazz) {
        logger.info("基于CGLib动态代理...");
        enhancer.setInterfaces(new Class[]{clazz});
        enhancer.setCallback(new InvocationHandler() {
            @Override
            public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
                return objectProxy.invoke(o, method, objects);
            }
        });
        return (T) enhancer.create();
    }
}
