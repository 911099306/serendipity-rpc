package com.serendipity.rpc.registry.zookeeper;

import com.serendipity.rpc.common.helper.RpcServiceHelper;
import com.serendipity.rpc.protocol.meta.ServiceMeta;
import com.serendipity.rpc.registry.api.RegistryService;
import com.serendipity.rpc.registry.api.config.RegistryConfig;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.details.JsonInstanceSerializer;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Random;

/**
 * 基于Zookeeper的注册服务
 *
 * @author serendipity
 * @version 1.0
 * @date 2024/2/18
 **/
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
     *  构建 CuratorFramework 客户端， 并初始化 ServiceDiscovery
     */
    @Override
    public void init(RegistryConfig registryConfig) throws Exception {
        CuratorFramework client = CuratorFrameworkFactory.newClient(registryConfig.getRegistryAddr(), new ExponentialBackoffRetry(BASE_SLEEP_TIME_MS, MAX_RETRIES));
        client.start();
        JsonInstanceSerializer<ServiceMeta> serializer = new JsonInstanceSerializer<>(ServiceMeta.class);
        this.serviceDiscovery = ServiceDiscoveryBuilder.builder(ServiceMeta.class)
                .client(client)
                .serializer(serializer)
                .basePath(ZK_BASE_PATH)
                .build();
        this.serviceDiscovery.start();
    }

    /**
     * 将 serviceMeta 元数据 注册到 Zookeeper
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
     * @param serviceName 服务名称
     * @param invokerHashCode HashCode 值 扩展负载均衡时使用
     * @throws Exception 抛出的异常
     */
    @Override
    public ServiceMeta discovery(String serviceName, int invokerHashCode) throws Exception {
        Collection<ServiceInstance<ServiceMeta>> serviceInstances = serviceDiscovery.queryForInstances(serviceName);
        ServiceInstance<ServiceMeta> instance = this.selectOneServiceInstance((List<ServiceInstance<ServiceMeta>>) serviceInstances);
        if (instance != null) {
            return instance.getPayload();
        }
        return null;
    }

    /**
     * 随机挑选一个服务
     * @param serviceInstances 在 Zookeeper 中注册的所有服务
     */
    private ServiceInstance<ServiceMeta> selectOneServiceInstance(List<ServiceInstance<ServiceMeta>> serviceInstances){
        if (serviceInstances == null || serviceInstances.isEmpty()){
            return null;
        }
        Random random = new Random();
        int index = random.nextInt(serviceInstances.size());
        return serviceInstances.get(index);
    }

    @Override
    public void destroy() throws IOException {
        serviceDiscovery.close();
    }
}
