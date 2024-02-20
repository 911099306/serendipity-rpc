package com.serendipity.rpc.reflect.bytebuddy;

import com.serendipity.rpc.reflect.api.ReflectInvoker;
import com.serendipity.rpc.spi.annotation.SPIClass;
import net.bytebuddy.ByteBuddy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

/**
 * @author serendipity
 * @version 1.0
 * @date 2024/2/20
 **/
@SPIClass
public class ByteBuddyReflectInvoker implements ReflectInvoker {

    private final Logger logger = LoggerFactory.getLogger(ByteBuddyReflectInvoker.class);
    @Override
    public Object invokeMethod(Object serviceBean, Class<?> serviceClass, String methodName, Class<?>[] parameterTypes, Object[] parameters) throws Throwable {
        logger.info("use bytebuddy reflect type invoke method...");
        Class<?> childClass = new ByteBuddy().subclass(serviceClass)
                .make()
                .load(ByteBuddyReflectInvoker.class.getClassLoader())
                .getLoaded();
        Object instance = childClass.getDeclaredConstructor().newInstance();
        Method method = childClass.getMethod(methodName, parameterTypes);
        method.setAccessible(true);
        return method.invoke(instance, parameters);
    }
}
