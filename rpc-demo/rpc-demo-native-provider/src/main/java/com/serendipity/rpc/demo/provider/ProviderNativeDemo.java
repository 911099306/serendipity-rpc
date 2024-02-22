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
    public void startRpcSingleServer(){
        RpcSingleServer singleServer = new RpcSingleServer("127.0.0.1:27880", "127.0.0.1:27880","127.0.0.1:2181", "zookeeper", "random","com.serendipity.rpc.demo", "jdk", 3000, 6000, true, 30000);
        singleServer.startNettyServer();
    }
}
