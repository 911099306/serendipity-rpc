package com.serendipity.rpc.proxy.api;

import com.serendipity.rpc.proxy.api.config.ProxyConfig;
import com.serendipity.rpc.proxy.api.object.ObjectProxy;

/**
 * 基础代理工厂类
 *
 * @author serendipity
 * @version 1.0
 * @date 2024/2/18
 **/
public abstract class BaseProxyFactory<T> implements ProxyFactory {

    protected ObjectProxy<T> objectProxy;

    @Override
    public <T> void init(ProxyConfig<T> proxyConfig) {
        this.objectProxy = new ObjectProxy(proxyConfig.getClazz(),
                proxyConfig.getServiceVersion(),
                proxyConfig.getServiceGroup(),
                proxyConfig.getSerializationType(),
                proxyConfig.getTimeout(),
                proxyConfig.getRegistryService(),
                proxyConfig.getConsumer(),
                proxyConfig.getAsync(),
                proxyConfig.getOneway(),
                proxyConfig.getEnableResultCache(),
                proxyConfig.getResultCacheExpire(),
                proxyConfig.getReflectType(),
                proxyConfig.getFallbackClassName(),
                proxyConfig.getFallbackClass());
    }
}
