package com.serendipity.rpc.spi.annotation;

import java.lang.annotation.*;

/**
 * @SPI 标注到加入SPI机制的接口上
 *
 * @author serendipity
 * @version 1.0
 * @date 2024/2/19
 **/
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface SPI {

    /**
     * 默认的实现方式
     */
    String value() default "";
}
