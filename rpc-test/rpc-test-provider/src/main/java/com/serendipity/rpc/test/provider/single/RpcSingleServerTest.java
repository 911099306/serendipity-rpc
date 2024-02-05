package com.serendipity.rpc.test.provider.single;

import com.serendipity.rpc.provider.RpcSingleServer;
import org.junit.Test;

/**
 * @author serendipity
 * @version 1.0
 * @date 2024/2/5
 **/
public class RpcSingleServerTest {

    @Test
    public void startRpcSingleServer(){
        RpcSingleServer singleServer = new RpcSingleServer("127.0.0.1:27880", "com.serendipity.rpc.test");
        singleServer.startNettyServer();
    }
}
