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

    public RpcSingleServer(String serverAddress, String registryAddress, String registryType, String scanPackage, String reflectType) {
        super(serverAddress,registryAddress, registryType, reflectType);
        try {
            this.handlerMap = RpcServiceScanner.doScannerWithRpcServiceAnnotationFilterAndRegistryService(this.host, this.port, scanPackage, registryService);
        } catch (Exception e) {
            logger.error("RPC Server init error! ", e);
        }
    }


}
