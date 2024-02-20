package com.serendipity.rpc.enhanced.loadbalancer.hash.weight;

import com.serendipity.rpc.loadbalancer.base.BaseEnhancedServiceLoadBalancer;
import com.serendipity.rpc.protocol.meta.ServiceMeta;
import com.serendipity.rpc.spi.annotation.SPIClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author serendipity
 * @version 1.0
 * @date 2024/2/21
 **/
@SPIClass
public class HashWeightServiceEnhancedLoadBalancer extends BaseEnhancedServiceLoadBalancer {
    private final Logger logger = LoggerFactory.getLogger(HashWeightServiceEnhancedLoadBalancer.class);
    @Override
    public ServiceMeta select(List<ServiceMeta> servers, int hashCode, String sourceIp) {
        logger.info("基于增强型加权Hash算法的负载均衡策略...");
        servers = this.getWeightServiceMetaList(servers);
        if (servers == null || servers.isEmpty()){
            return null;
        }
        int index = Math.abs(hashCode) % servers.size();
        return servers.get(index);
    }
}
