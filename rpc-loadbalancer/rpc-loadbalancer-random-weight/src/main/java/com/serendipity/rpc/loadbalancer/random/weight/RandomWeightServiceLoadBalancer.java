package com.serendipity.rpc.loadbalancer.random.weight;

import com.serendipity.rpc.loadbalancer.api.ServiceLoadBalancer;
import com.serendipity.rpc.spi.annotation.SPIClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Random;

/**
 * 根据传入的 hashcode ，从前 hashCode % servers.size() 个服务提供者中，随机挑选一个，缩小随机的范围
 * @author serendipity
 * @version 1.0
 * @date 2024/2/20
 **/
@SPIClass
public class RandomWeightServiceLoadBalancer<T> implements ServiceLoadBalancer<T> {

    private final Logger logger = LoggerFactory.getLogger(RandomWeightServiceLoadBalancer.class);

    @Override
    public T select(List<T> servers, int hashCode, String sourceIp) {
        logger.info("基于加权随机算法的负载均衡策略...");
        if (servers == null || servers.isEmpty()) {
            return null;
        }
        hashCode = Math.abs(hashCode);
        // 根据传入的 hashcode ，从前 hashCode % servers.size() 个服务提供者中，随机挑选一个，缩小随机的范围
        int count = hashCode % servers.size();
        if (count <= 1) {
            count = servers.size();
        }
        Random random = new Random();
        int index = random.nextInt(count);
        return servers.get(index);
    }
}
