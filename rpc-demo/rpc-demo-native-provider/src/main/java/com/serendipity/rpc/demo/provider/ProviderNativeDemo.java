package com.serendipity.rpc.demo.provider;

import com.serendipity.rpc.provider.RpcSingleServer;
import org.junit.Test;

/**
 * @author serendipity
 * @version 1.0
 * @date 2024/2/21
 **/
public class ProviderNativeDemo {
    @Test
    public void startRpcSingleServer() {
        RpcSingleServer singleServer = new RpcSingleServer("127.0.0.1:27880", "127.0.0.1:27880", "127.0.0.1:2181", "zookeeper", "random", "com.serendipity.rpc.demo", "jdk", 3000, 6000, false, 30000, 16, 16, "print", 1, "strategy_default",false,65536,false,"guava", 1, 5000, "fallback" /**direct/fallback/exception**/,true,"counter",1,5000);
        singleServer.startNettyServer();
    }
}
