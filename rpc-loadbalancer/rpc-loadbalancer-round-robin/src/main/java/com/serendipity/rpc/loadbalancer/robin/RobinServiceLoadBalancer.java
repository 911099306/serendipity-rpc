package com.serendipity.rpc.loadbalancer.robin;

import com.serendipity.rpc.loadbalancer.api.ServiceLoadBalancer;
import com.serendipity.rpc.spi.annotation.SPIClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author serendipity
 * @version 1.0
 * @date 2024/2/20
 **/
@SPIClass
public class RobinServiceLoadBalancer<T> implements ServiceLoadBalancer<T> {
    private final Logger logger = LoggerFactory.getLogger(RobinServiceLoadBalancer.class);
    private volatile AtomicInteger atomicInteger = new AtomicInteger(0);

    @Override
    public T select(List<T> servers, int hashCode, String sourceIp) {
        logger.info("基于轮询算法的负载均衡策略...");
        if (servers == null || servers.isEmpty()) {
            return null;
        }
        int count = servers.size();
        int index = atomicInteger.incrementAndGet();
        if (index >= Integer.MAX_VALUE - 10000) {
            atomicInteger.set(0);
        }
        return servers.get(index % count);
    }
}
