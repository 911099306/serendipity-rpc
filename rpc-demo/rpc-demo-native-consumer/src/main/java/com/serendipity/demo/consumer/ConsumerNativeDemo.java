package com.serendipity.demo.consumer;

import com.serendipity.rpc.consumer.RpcClient;
import com.serendipity.rpc.demo.api.DemoService;
import com.serendipity.rpc.proxy.api.async.IAsyncObjectProxy;
import com.serendipity.rpc.proxy.api.future.RPCFuture;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author serendipity
 * @version 1.0
 * @date 2024/2/21
 **/
public class ConsumerNativeDemo {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConsumerNativeDemo.class);

    private RpcClient rpcClient;

    @Before
    public void initRpcClient(){
        rpcClient = new RpcClient("127.0.0.1:2181", "zookeeper", "enhanced_leastconnections","cglib","1.0.0", "serendipity", "jdk", 3000, false, false,3000, 6000);
    }


    @Test
    public void testInterfaceRpc() throws InterruptedException {
        DemoService demoService = rpcClient.create(DemoService.class);
        String result = demoService.hello("serendipity");
        LOGGER.info("返回的结果数据===>>> " + result);
        //rpcClient.shutdown();
        while (true){
            Thread.sleep(1000);
        }
    }

    @Test
    public void testAsyncInterfaceRpc() throws Exception {
        IAsyncObjectProxy demoService = rpcClient.createAsync(DemoService.class);
        RPCFuture future = demoService.call("hello", "serendipity");
        LOGGER.info("返回的结果数据===>>> " + future.get());
        rpcClient.shutdown();
    }
}
