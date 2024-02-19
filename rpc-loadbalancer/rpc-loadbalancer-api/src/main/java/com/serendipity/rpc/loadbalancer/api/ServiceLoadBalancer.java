package com.serendipity.rpc.loadbalancer.api;

import java.util.List;

/**
 * 负载均衡接口
 *
 * @author serendipity
 * @version 1.0
 * @date 2024/2/19
 **/
public interface ServiceLoadBalancer<T> {

    /**
     * 以负载均衡的方式选取一个服务节点
     * @param servers 服务列表
     * @param hashCode hash 值
     * @return 可用的服务节点
     */
    T select(List<T> servers, int hashCode);
}
