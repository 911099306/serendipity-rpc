package com.serendipity.rpc.registry.api;

import com.serendipity.rpc.protocol.meta.ServiceMeta;
import com.serendipity.rpc.registry.api.config.RegistryConfig;
import com.serendipity.rpc.spi.annotation.SPI;

import java.io.IOException;
import java.util.List;

/**
 * 注册配置类
 *
 * @author serendipity
 * @version 1.0
 * @date 2024/2/18
 **/
@SPI
public interface RegistryService {

    /**
     * 服务注册
     * @param serviceMeta 服务元数据
     * @throws Exception 抛出异常
     */
    void register(ServiceMeta serviceMeta) throws Exception;

    /**
     * 服务取消注册
     * @param serviceMeta 服务元数据
     * @throws Exception 抛出异常
     */
    void unRegister(ServiceMeta serviceMeta) throws Exception;

    /**
     * 服务发现
     * @param serviceName 服务名称
     * @param invokerHashCode HashCode 值, 负载均衡
     * @return 服务元数据
     * @throws Exception 抛出异常
     */
    ServiceMeta discovery(String serviceName, int invokerHashCode, String sourceIp) throws Exception;


    /**
     * 从多个元数据列表中根据一定的规则获取一个元数据
     * @param serviceMetaList 元数据列表
     * @return 某个特定的元数据
     */
    ServiceMeta select(List<ServiceMeta> serviceMetaList, int invokerHashCode, String sourceIp);

    /**
     * 服务销毁
     * @throws IOException 抛出异常
     */
    void destroy() throws IOException;

    /**
     * 默认初始化方法
     */
    default void init(RegistryConfig registryConfig) throws Exception{

    }
}
