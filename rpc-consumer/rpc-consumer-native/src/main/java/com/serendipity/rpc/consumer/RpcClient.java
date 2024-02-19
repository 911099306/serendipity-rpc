package com.serendipity.rpc.consumer;

import com.serendipity.rpc.consumer.common.RpcConsumer;
import com.serendipity.rpc.proxy.api.async.IAsyncObjectProxy;
import com.serendipity.rpc.proxy.api.config.ProxyConfig;
import com.serendipity.rpc.proxy.api.object.ObjectProxy;
import com.serendipity.rpc.proxy.jdk.JdkProxyFactory;
import com.serendipity.rpc.registry.api.RegistryService;
import com.serendipity.rpc.registry.api.config.RegistryConfig;
import com.serendipity.rpc.registry.zookeeper.ZookeeperRegistryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 服务消费客户端
 *
 * @author serendipity
 * @version 1.0
 * @date 2024/2/18
 **/
public class RpcClient {
    private final Logger logger = LoggerFactory.getLogger(RpcClient.class);

    /**
     * 服务版本
     */
    private String serviceVersion;
    /**
     * 服务分组
     */
    private String serviceGroup;
    /**
     * 序列化类型
     */
    private String serializationType;
    /**
     * 超时时间
     */
    private long timeout;

    /**
     * 注册服务
     */
    private RegistryService registryService;

    /**
     * 是否异步调用
     */
    private boolean async;

    /**
     * 是否单向调用
     */
    private boolean oneway;

    public RpcClient(String registryAddress, String registryType, String serviceVersion, String serviceGroup, String serializationType, long timeout, boolean async, boolean oneway) {
        this.serviceVersion = serviceVersion;
        this.timeout = timeout;
        this.serviceGroup = serviceGroup;
        this.serializationType = serializationType;
        this.async = async;
        this.oneway = oneway;
        this.registryService = this.getRegistryService(registryAddress, registryType);
    }

    /**
     * 根据 registryType 生成相应的注册服务
     *
     * @param registryAddress 注册中心地址
     * @param registryType    注册中心类型
     */
    private RegistryService getRegistryService(String registryAddress, String registryType) {
        if (registryType == null) {
            throw new IllegalArgumentException("RpcClient: registry type is null~~");
        }
        // TODO：SPI扩展
        RegistryService registryService = new ZookeeperRegistryService();
        try {
            registryService.init(new RegistryConfig(registryAddress, registryType));
        } catch (Exception e) {
            logger.info("RpcClient: RpcClient init registry service throws exception:{}", e);
        }
        return registryService;
    }

    public <T> T create(Class<T> interfaceClass) {
        JdkProxyFactory<T> proxyFactory = new JdkProxyFactory<T>();
        proxyFactory.init(new ProxyConfig<>(interfaceClass, serviceVersion, serviceGroup, timeout, registryService, RpcConsumer.getInstance(), serializationType, async, oneway));
        return proxyFactory.getProxy(interfaceClass);
    }

    public <T> IAsyncObjectProxy createAsync(Class<T> interfaceClass) {
        return new ObjectProxy<T>(interfaceClass, serviceVersion, serviceGroup, serializationType, timeout, registryService, RpcConsumer.getInstance(), async, oneway);
    }

    public void shutdown() {
        RpcConsumer.getInstance().close();
    }
}
