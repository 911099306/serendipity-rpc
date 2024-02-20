package com.serendipity.rpc.proxy.bytebuddy;

import com.serendipity.rpc.proxy.api.BaseProxyFactory;
import com.serendipity.rpc.proxy.api.ProxyFactory;
import com.serendipity.rpc.spi.annotation.SPIClass;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.implementation.InvocationHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author serendipity
 * @version 1.0
 * @date 2024/2/20
 **/
@SPIClass
public class ByteBuddyProxyFactory<T> extends BaseProxyFactory<T> implements ProxyFactory {
    private final Logger logger = LoggerFactory.getLogger(ByteBuddyProxyFactory.class);
    @Override
    public <T> T getProxy(Class<T> clazz) {
        try{
            logger.info("基于ByteBuddy动态代理...");
            return (T) new ByteBuddy().subclass(Object.class)
                    .implement(clazz)
                    .intercept(InvocationHandlerAdapter.of(objectProxy))
                    .make()
                    .load(ByteBuddyProxyFactory.class.getClassLoader())
                    .getLoaded()
                    .getDeclaredConstructor()
                    .newInstance();
        }catch (Exception e){
            logger.error("bytebuddy proxy throws exception:{}", e);
        }
        return null;
    }
}
