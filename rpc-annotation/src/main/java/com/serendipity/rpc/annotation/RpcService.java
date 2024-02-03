package com.serendipity.rpc.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author serendipity
 * @version 1.0.0
 * @date 2024/2/3
 **/
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface RpcService {

    /**
     * 接口的 Class
     */
    Class<?> interfaceClass() default void.class;

    /**
     * 接口的 ClassName
     */
    String interfaceClassName() default "";

    /**
     * 版本号
     */
    String version() default "1.0.0";

    /**
     * 服务分组，默认为空
     */
    String group() default "";
}
