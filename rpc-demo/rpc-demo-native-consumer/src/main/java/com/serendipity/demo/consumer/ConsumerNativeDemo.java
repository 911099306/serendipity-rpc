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
    public void initRpcClient() {
        rpcClient = new RpcClient("127.0.0.1:2181", "zookeeper", "random", "cglib", "1.0.0", "serendipity", "protostuff", 3000, false, false, 30000, 60000, 10000, 3, false, 10000, false, "127.0.0.1:278800", false, 16, 16, "print", true, 2);
    }


    @Test
    public void testInterfaceRpc1() throws InterruptedException {
        DemoService demoService = rpcClient.create(DemoService.class);
        for (int i = 0; i < 10; i++) {
            String result = demoService.hello("serendipity~~~~~~~~~~~~~~~~~");
            LOGGER.info("返回的结果数据===>>> " + result);
        }
        // rpcClient.shutdown();
        while (true) {
            Thread.sleep(100000);
        }
    }

    @Test
    public void testInterfaceRpc2() throws InterruptedException {
        DemoService demoService = rpcClient.create(DemoService.class);
        // String result = demoService.hello("serendipity~~~~~~~~~~~~~~~~~22");
        // LOGGER.info("返回的结果数据===>>> " + result);

        // rpcClient.shutdown();
        while (true) {
            Thread.sleep(100000);
        }
    }

    @Test
    public void testInterfaceRpc3() throws InterruptedException {
        DemoService demoService = rpcClient.create(DemoService.class);
        // String result = demoService.hello("serendipity~~~~~~~~~~~~~~~~~22");
        // LOGGER.info("返回的结果数据===>>> "  + result);
        // rpcClient.shutdown();
        while (true) {
            Thread.sleep(100000);
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
