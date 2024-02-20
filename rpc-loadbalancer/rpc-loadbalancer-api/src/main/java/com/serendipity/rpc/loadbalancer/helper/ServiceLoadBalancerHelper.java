package com.serendipity.rpc.loadbalancer.helper;

import com.serendipity.rpc.protocol.meta.ServiceMeta;
import org.apache.curator.x.discovery.ServiceInstance;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 将List<ServiceInstance<ServiceMeta>> 列表转化为 List<ServiceMeta> 列表
 *
 * @author serendipity
 * @version 1.0
 * @date 2024/2/20
 **/
public class ServiceLoadBalancerHelper {
    private static volatile List<ServiceMeta> cacheServiceMeta = new CopyOnWriteArrayList<>();

    /**
     * 将List<ServiceInstance<ServiceMeta>> 列表转化为 List<ServiceMeta> 列表
     */
    public static List<ServiceMeta> getServiceMetaList(List<ServiceInstance<ServiceMeta>> serviceInstances){
        if (serviceInstances == null || serviceInstances.isEmpty() || cacheServiceMeta.size() == serviceInstances.size()){
            return cacheServiceMeta;
        }
        //先清空cacheServiceMeta中的数据
        cacheServiceMeta.clear();
        serviceInstances.stream().forEach((serviceMetaServiceInstance) -> {
            cacheServiceMeta.add(serviceMetaServiceInstance.getPayload());
        });
        return cacheServiceMeta;
    }
}
