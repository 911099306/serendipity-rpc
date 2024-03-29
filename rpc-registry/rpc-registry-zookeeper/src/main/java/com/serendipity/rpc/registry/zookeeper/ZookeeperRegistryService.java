package com.serendipity.rpc.registry.zookeeper;

import com.serendipity.rpc.common.helper.RpcServiceHelper;
import com.serendipity.rpc.constants.RpcConstants;
import com.serendipity.rpc.loadbalancer.api.ServiceLoadBalancer;
import com.serendipity.rpc.loadbalancer.helper.ServiceLoadBalancerHelper;
import com.serendipity.rpc.loadbalancer.random.RandomServiceLoadBalancer;
import com.serendipity.rpc.protocol.meta.ServiceMeta;
import com.serendipity.rpc.registry.api.RegistryService;
import com.serendipity.rpc.registry.api.config.RegistryConfig;
import com.serendipity.rpc.spi.annotation.SPIClass;
import com.serendipity.rpc.spi.loader.ExtensionLoader;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.details.JsonInstanceSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

/**
 * 基于Zookeeper的注册服务
 *
 * @author serendipity
 * @version 1.0
 * @date 2024/2/18
 **/
@SPIClass
public class ZookeeperRegistryService implements RegistryService {

    /**
     * 初始化客户端时，进行连接重试的间隔时间
     */
    public static final int BASE_SLEEP_TIME_MS = 1000;

    /**
     * 初始化客户端时，进行连接重试的最大次数
     */
    public static final int MAX_RETRIES = 3;

    /**
     * 服务注册到Zookeeper的根路径
     */
    public static final String ZK_BASE_PATH = "/serendipity_rpc";

    /**
     * 服务注册与发现的 ServiceDiscovery 实例
     */
    private ServiceDiscovery<ServiceMeta> serviceDiscovery;

    /**
      * 负载均衡接口
    */
    private ServiceLoadBalancer<ServiceMeta> serviceLoadBalancer;

    // /**
    //  * 负载均衡接口
    //  */
    // private ServiceLoadBalancer<ServiceInstance<ServiceMeta>> serviceLoadBalancer;

    // /**
    //  * 增强负载均衡接口
    //  */
    // private ServiceLoadBalancer<ServiceMeta> serviceEnhancedLoadBalancer;

    /**
     * 构建 CuratorFramework 客户端， 并初始化 ServiceDiscovery
     */
    private final Logger logger = LoggerFactory.getLogger(ZookeeperRegistryService.class);

    @Override
    public void init(RegistryConfig registryConfig) throws Exception {
        logger.info("使用zookeeper作为注册中心...");
        CuratorFramework client = CuratorFrameworkFactory.newClient(registryConfig.getRegistryAddr(), new ExponentialBackoffRetry(BASE_SLEEP_TIME_MS, MAX_RETRIES));
        client.start();
        JsonInstanceSerializer<ServiceMeta> serializer = new JsonInstanceSerializer<>(ServiceMeta.class);
        this.serviceDiscovery = ServiceDiscoveryBuilder.builder(ServiceMeta.class)
                .client(client)
                .serializer(serializer)
                .basePath(ZK_BASE_PATH)
                .build();
        this.serviceDiscovery.start();
        // 根据前缀 选择负载均衡方式

        this.serviceLoadBalancer = ExtensionLoader.getExtension(ServiceLoadBalancer.class, registryConfig.getRegistryLoadBalanceType());
    }

    /**
     * 将 serviceMeta 元数据 注册到 Zookeeper
     *
     * @param serviceMeta 服务元数据
     * @throws Exception 抛出的异常
     */
    @Override
    public void register(ServiceMeta serviceMeta) throws Exception {
        ServiceInstance<ServiceMeta> serviceInstance = ServiceInstance
                .<ServiceMeta>builder()
                .name(RpcServiceHelper.buildServiceKey(serviceMeta.getServiceName(), serviceMeta.getServiceVersion(), serviceMeta.getServiceGroup()))
                .address(serviceMeta.getServiceAddr())
                .port(serviceMeta.getServicePort())
                .payload(serviceMeta)
                .build();
        serviceDiscovery.registerService(serviceInstance);
    }

    /**
     * 移除 Zookeeper 中注册的对应的元数据
     *
     * @param serviceMeta 服务元数据
     * @throws Exception 抛出的异常
     */
    @Override
    public void unRegister(ServiceMeta serviceMeta) throws Exception {
        ServiceInstance<ServiceMeta> serviceInstance = ServiceInstance
                .<ServiceMeta>builder()
                .name(serviceMeta.getServiceName())
                .address(serviceMeta.getServiceAddr())
                .port(serviceMeta.getServicePort())
                .payload(serviceMeta)
                .build();
        serviceDiscovery.unregisterService(serviceInstance);
    }

    /**
     * 根据传入的 serviceName 和 invokerHashCode 从 Zookeeper 中获取对应的ServiceMeta元数据
     *
     * @param serviceName     服务名称
     * @param invokerHashCode HashCode 值 扩展负载均衡时使用
     * @throws Exception 抛出的异常
     */
    @Override
    public ServiceMeta discovery(String serviceName, int invokerHashCode, String sourceIp) throws Exception {
        Collection<ServiceInstance<ServiceMeta>> serviceInstances = serviceDiscovery.queryForInstances(serviceName);
        return this.serviceLoadBalancer.select(ServiceLoadBalancerHelper.getServiceMetaList((List<ServiceInstance<ServiceMeta>>) serviceInstances), invokerHashCode, sourceIp);
    }

    @Override
    public ServiceMeta select(List<ServiceMeta> serviceMetaList, int invokerHashCode, String sourceIp) {
        return this.serviceLoadBalancer.select(serviceMetaList, invokerHashCode, sourceIp);
    }

    // private ServiceMeta getServiceMetaInstance(int invokerHashCode, String sourceIp, List<ServiceInstance<ServiceMeta>> serviceInstances) {
    //     ServiceInstance<ServiceMeta> instance = this.serviceLoadBalancer.select(serviceInstances, invokerHashCode, sourceIp);
    //     if (instance != null) {
    //         return instance.getPayload();
    //     }
    //     return null;
    // }

    @Override
    public List<ServiceMeta> discoveryAll() throws Exception {
        List<ServiceMeta> serviceMetaList = new ArrayList<>();
        Collection<String> names = serviceDiscovery.queryForNames();
        if (names == null || names.isEmpty()) return serviceMetaList;
        for (String name : names){
            Collection<ServiceInstance<ServiceMeta>> serviceInstances = serviceDiscovery.queryForInstances(name);
            List<ServiceMeta> list = this.getServiceMetaFromServiceInstance((List<ServiceInstance<ServiceMeta>>) serviceInstances);
            serviceMetaList.addAll(list);
        }
        return serviceMetaList;
    }

    private List<ServiceMeta> getServiceMetaFromServiceInstance(List<ServiceInstance<ServiceMeta>> serviceInstances){
        List<ServiceMeta> list = new ArrayList<>();
        if (serviceInstances == null || serviceInstances.isEmpty()) return list;
        IntStream.range(0, serviceInstances.size()).forEach((i)->{
            ServiceInstance<ServiceMeta> serviceInstance = serviceInstances.get(i);
            list.add(serviceInstance.getPayload());
        });
        return list;
    }

    @Override
    public void destroy() throws IOException {
        serviceDiscovery.close();
    }
}
