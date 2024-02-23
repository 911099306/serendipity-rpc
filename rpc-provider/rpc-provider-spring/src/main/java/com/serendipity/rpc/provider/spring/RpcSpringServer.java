package com.serendipity.rpc.provider.spring;

import com.serendipity.rpc.annotation.RpcService;
import com.serendipity.rpc.common.helper.RpcServiceHelper;
import com.serendipity.rpc.constants.RpcConstants;
import com.serendipity.rpc.protocol.meta.ServiceMeta;
import com.serendipity.rpc.provider.common.server.base.BaseServer;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Map;

/**
 * 基于Spring启动RPC服务
 *
 * @author serendipity
 * @version 1.0
 * @date 2024/2/22
 **/
public class RpcSpringServer extends BaseServer implements ApplicationContextAware, InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(RpcSpringServer.class);

    public RpcSpringServer(String serverAddress, String serverRegistryAddress, String registryAddress, String registryType, String registryLoadBalanceType, String reflectType, int heartbeatInterval, int scanNotActiveChannelInterval, boolean enableResultCache, int resultCacheExpire, int corePoolSize, int maximumPoolSize,String flowType) {
        super(serverAddress, serverRegistryAddress, registryAddress, registryType, registryLoadBalanceType, reflectType, heartbeatInterval, scanNotActiveChannelInterval, enableResultCache, resultCacheExpire, corePoolSize, maximumPoolSize,flowType);
    }

    /**
     * 对@RpcService注解进行扫描，并将服务提供者的元数据信心注册到注册中心，
     * 在InitializingBean的afterPropertiesSet()方法中调用startNettyServer()方法启动服务提供者。
     *
     * @param ctx
     * @throws BeansException
     */
    @Override
    public void setApplicationContext(ApplicationContext ctx) throws BeansException {
        Map<String, Object> serviceBeanMap = ctx.getBeansWithAnnotation(RpcService.class);
        if (MapUtils.isNotEmpty(serviceBeanMap)) {
            for (Object serviceBean : serviceBeanMap.values()) {
                RpcService rpcService = serviceBean.getClass().getAnnotation(RpcService.class);
                ServiceMeta serviceMeta = new ServiceMeta(this.getServiceName(rpcService), rpcService.version(), rpcService.group(), host, port, getWeight(rpcService.weight()));
                handlerMap.put(RpcServiceHelper.buildServiceKey(serviceMeta.getServiceName(), serviceMeta.getServiceVersion(), serviceMeta.getServiceGroup()), serviceBean);
                try {
                    registryService.register(serviceMeta);
                } catch (Exception e) {
                    logger.error("rpc server init spring exception:{}", e);
                }
            }
        }
    }

    /**
     * 获取合法权重
     *
     * @param weight 权重
     * @return 合法权重
     */
    private int getWeight(int weight) {
        if (weight < RpcConstants.SERVICE_WEIGHT_MIN) {
            weight = RpcConstants.SERVICE_WEIGHT_MIN;
        }
        if (weight > RpcConstants.SERVICE_WEIGHT_MAX) {
            weight = RpcConstants.SERVICE_WEIGHT_MAX;
        }
        return weight;
    }

    /**
     * 获取服务名称
     *
     * @param rpcService RpcService注解
     * @return 服务名称
     */
    private String getServiceName(RpcService rpcService) {
        // 优先使用interfaceClass
        Class clazz = rpcService.interfaceClass();
        if (clazz == void.class) {
            return rpcService.interfaceClassName();
        }
        String serviceName = clazz.getName();
        if (serviceName == null || serviceName.trim().isEmpty()) {
            serviceName = rpcService.interfaceClassName();
        }
        return serviceName;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.startNettyServer();
    }
}
