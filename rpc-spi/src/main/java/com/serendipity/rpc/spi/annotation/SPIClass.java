package com.serendipity.rpc.spi.annotation;

import java.lang.annotation.*;

/**
 * @SPIClass 标注到加入SPI机制的 实现类 上
 *
 * @author serendipity
 * @version 1.0
 * @date 2024/2/19
 **/
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface SPIClass {
}
