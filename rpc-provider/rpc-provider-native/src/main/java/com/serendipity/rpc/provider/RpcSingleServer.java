package com.serendipity.rpc.provider;

import com.serendipity.rpc.provider.common.scanner.RpcServiceScanner;
import com.serendipity.rpc.provider.common.server.base.BaseServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author serendipity
 * @version 1.0
 * @date 2024/2/5
 **/
public class RpcSingleServer extends BaseServer {

    private final Logger logger = LoggerFactory.getLogger(RpcSingleServer.class);

    public RpcSingleServer(String serverAddress, String serverRegistryAddress, String registryAddress, String registryType,
                           String registryLoadBalanceType, String scanPackage, String reflectType, int heartbeatInterval,
                           int scanNotActiveChannelInterval, boolean enableResultCache, int resultCacheExpire,
                           int corePoolSize, int maximumPoolSize, String flowType, int maxConnections, String disuseStrategyType,
                           boolean enableBuffer, int bufferSize, boolean enableRateLimiter, String rateLimiterType,
                           int permits, int milliSeconds, String rateLimiterFailStrategy,
                           boolean enableFusing, String fusingType, double totalFailure, int fusingMilliSeconds) {
        super(serverAddress, serverRegistryAddress, registryAddress, registryType, registryLoadBalanceType, reflectType,
                heartbeatInterval, scanNotActiveChannelInterval, enableResultCache, resultCacheExpire, corePoolSize,
                maximumPoolSize, flowType, maxConnections, disuseStrategyType, enableBuffer, bufferSize, enableRateLimiter,
                rateLimiterType, permits, milliSeconds, rateLimiterFailStrategy, enableFusing, fusingType, totalFailure, fusingMilliSeconds);
        try {
            this.handlerMap = RpcServiceScanner.doScannerWithRpcServiceAnnotationFilterAndRegistryService(this.host, this.port, scanPackage, registryService);
        } catch (Exception e) {
            logger.error("RPC Server init error! ", e);
        }
    }


}
