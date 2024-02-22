package com.serendipity.rpc.consumer;

import com.serendipity.rpc.consumer.common.RpcConsumer;
import com.serendipity.rpc.proxy.api.ProxyFactory;
import com.serendipity.rpc.proxy.api.async.IAsyncObjectProxy;
import com.serendipity.rpc.proxy.api.config.ProxyConfig;
import com.serendipity.rpc.proxy.api.object.ObjectProxy;
import com.serendipity.rpc.proxy.jdk.JdkProxyFactory;
import com.serendipity.rpc.registry.api.RegistryService;
import com.serendipity.rpc.registry.api.config.RegistryConfig;
import com.serendipity.rpc.registry.zookeeper.ZookeeperRegistryService;
import com.serendipity.rpc.spi.factory.ExtensionFactory;
import com.serendipity.rpc.spi.loader.ExtensionLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

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

    /**
     * 代理方式
     */
    private String proxy;

    /**
     * 心跳间隔时间，默认30秒
     */
    private int heartbeatInterval;

    /**
     * 扫描空闲连接时间，默认60秒
     */
    private int scanNotActiveChannelInterval;

    /**
     * 重试间隔时间
     */
    private int retryInterval = 1000;

    /**
     * 重试次数
     */
    private int retryTimes = 3;

    /**
     * 是否开启结果缓存
     */
    private boolean enableResultCache;

    /**
     * 缓存结果的时长，单位是毫秒
     */
    private int resultCacheExpire;

    public RpcClient(String registryAddress, String registryType, String registryLoadBalanceType, String proxy, String serviceVersion, String serviceGroup, String serializationType, long timeout, boolean async, boolean oneway, int heartbeatInterval, int scanNotActiveChannelInterval, int retryInterval, int retryTimes, boolean enableResultCache, int resultCacheExpire) {
        this.serviceVersion = serviceVersion;
        this.proxy = proxy;
        this.timeout = timeout;
        this.serviceGroup = serviceGroup;
        this.serializationType = serializationType;
        this.async = async;
        this.oneway = oneway;
        this.heartbeatInterval = heartbeatInterval;
        this.scanNotActiveChannelInterval = scanNotActiveChannelInterval;
        this.retryInterval = retryInterval;
        this.retryTimes = retryTimes;
        this.enableResultCache = enableResultCache;
        this.resultCacheExpire = resultCacheExpire;
        this.registryService = this.getRegistryService(registryAddress, registryType, registryLoadBalanceType);
    }

    /**
     * 根据 registryType 生成相应的注册服务
     *
     * @param registryAddress 注册中心地址
     * @param registryType    注册中心类型
     */
    private RegistryService getRegistryService(String registryAddress, String registryType, String registryLoadBalanceType) {
        if (StringUtils.isEmpty(registryType)) {
            throw new IllegalArgumentException("registry type is null~~");
        }
        // TODO：SPI扩展
        // RegistryService registryService = new ZookeeperRegistryService();
        RegistryService registryService = ExtensionLoader.getExtension(RegistryService.class, registryType);
        try {
            registryService.init(new RegistryConfig(registryAddress, registryType, registryLoadBalanceType));
        } catch (Exception e) {
            logger.error("RpcClient init registry service throws exception:{}", e);
            throw new RuntimeException(e.getMessage(), e);
        }
        return registryService;
    }

    public <T> T create(Class<T> interfaceClass) {
        // JdkProxyFactory<T> proxyFactory = new JdkProxyFactory<T>();
        ProxyFactory proxyFactory = ExtensionLoader.getExtension(ProxyFactory.class, proxy);
        proxyFactory.init(new ProxyConfig(interfaceClass, serviceVersion, serviceGroup, serializationType, timeout, registryService, RpcConsumer.getInstance(heartbeatInterval, scanNotActiveChannelInterval, retryInterval, retryTimes), async, oneway, enableResultCache, resultCacheExpire));
        return proxyFactory.getProxy(interfaceClass);
    }

    public <T> IAsyncObjectProxy createAsync(Class<T> interfaceClass) {
        return new ObjectProxy<T>(interfaceClass, serviceVersion, serviceGroup, serializationType, timeout, registryService, RpcConsumer.getInstance(heartbeatInterval, scanNotActiveChannelInterval, retryInterval, retryTimes), async, oneway, enableResultCache, resultCacheExpire);
    }

    public void shutdown() {
        RpcConsumer.getInstance(heartbeatInterval, scanNotActiveChannelInterval, retryInterval, retryTimes).close();
    }
}
