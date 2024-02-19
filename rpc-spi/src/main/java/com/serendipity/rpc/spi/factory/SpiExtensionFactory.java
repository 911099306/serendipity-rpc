package com.serendipity.rpc.spi.factory;

import com.serendipity.rpc.spi.annotation.SPI;
import com.serendipity.rpc.spi.annotation.SPIClass;
import com.serendipity.rpc.spi.loader.ExtensionLoader;

import java.util.Optional;

/**
 * 基于SPI实现的扩展类加载器工厂类
 *
 * @author serendipity
 * @version 1.0
 * @date 2024/2/19
 **/
@SPIClass
public class SpiExtensionFactory implements ExtensionFactory{
    @Override
    public <T> T getExtension(String key, Class<T> clazz) {
        return Optional.ofNullable(clazz)
                .filter(Class::isInterface)
                .filter(cls -> cls.isAnnotationPresent(SPI.class))
                .map(ExtensionLoader::getExtensionLoader)
                .map(ExtensionLoader::getDefaultSpiClassInstance)
                .orElse(null);
    }
}
