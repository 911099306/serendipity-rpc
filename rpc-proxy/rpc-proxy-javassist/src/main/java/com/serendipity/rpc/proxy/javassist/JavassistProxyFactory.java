package com.serendipity.rpc.proxy.javassist;

import com.serendipity.rpc.proxy.api.BaseProxyFactory;
import com.serendipity.rpc.proxy.api.ProxyFactory;
import com.serendipity.rpc.spi.annotation.SPIClass;
import javassist.util.proxy.MethodHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

/**
 * @author serendipity
 * @version 1.0
 * @date 2024/2/20
 **/
@SPIClass
public class JavassistProxyFactory <T> extends BaseProxyFactory<T> implements ProxyFactory {
    private final Logger logger = LoggerFactory.getLogger(JavassistProxyFactory.class);
    private javassist.util.proxy.ProxyFactory proxyFactory = new javassist.util.proxy.ProxyFactory();

    @Override
    public <T> T getProxy(Class<T> clazz) {
        try {
            logger.info("基于Javassist动态代理...");
            //设置代理类的父类
            proxyFactory.setInterfaces(new Class[]{clazz});
            proxyFactory.setHandler(new MethodHandler() {
                @Override
                public Object invoke(Object self, Method thisMethod, Method proceed, Object[] args) throws Throwable {
                    return objectProxy.invoke(self, thisMethod, args);
                }
            });
            // 通过字节码技术动态创建子类实例
            return (T) proxyFactory.createClass().newInstance();
        }catch (Exception e){
            logger.error("javassist proxy throws exception:{}", e);
        }
        return null;
    }
}
