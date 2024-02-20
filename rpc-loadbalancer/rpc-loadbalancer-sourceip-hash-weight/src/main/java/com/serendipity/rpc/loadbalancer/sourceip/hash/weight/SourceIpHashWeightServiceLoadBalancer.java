package com.serendipity.rpc.loadbalancer.sourceip.hash.weight;

import com.serendipity.rpc.loadbalancer.api.ServiceLoadBalancer;
import com.serendipity.rpc.spi.annotation.SPIClass;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author serendipity
 * @version 1.0
 * @date 2024/2/20
 **/
@SPIClass
public class SourceIpHashWeightServiceLoadBalancer<T> implements ServiceLoadBalancer<T> {

    private final Logger logger = LoggerFactory.getLogger(SourceIpHashWeightServiceLoadBalancer.class);

    @Override
    public T select(List<T> servers, int hashCode, String sourceIp) {
        logger.info("基于源IP地址加权Hash的负载均衡策略...");
        if (servers == null || servers.isEmpty()) {
            return null;
        }
        // 传入的IP地址为空，则默认返回第一个服务实例m
        if (StringUtils.isEmpty(sourceIp)) {
            return servers.get(0);
        }
        int count = Math.abs(hashCode) % servers.size();
        if (count == 0) {
            count = servers.size();
        }
        int resultHashCode = Math.abs(sourceIp.hashCode() + hashCode);
        return servers.get(resultHashCode % count);
    }
}
