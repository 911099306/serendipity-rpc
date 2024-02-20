package com.serendipity.test.consumer;

import com.serendipity.rpc.consumer.RpcClient;
import com.serendipity.rpc.proxy.api.async.IAsyncObjectProxy;
import com.serendipity.rpc.proxy.api.future.RPCFuture;
import com.serendipity.rpc.test.api.DemoService;
import com.sun.org.apache.bcel.internal.generic.NEW;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 测试Java原生启动服务消费者
 *
 * @author serendipity
 * @version 1.0
 * @date 2024/2/18
 **/
public class RpcConsumerNativeTest {

    private static final Logger logger = LoggerFactory.getLogger(RpcConsumerNativeTest.class);

    public static void main(String[] args) {
        RpcClient rpcClient = new RpcClient("127.0.0.1:2181", "zookeeper","zkconsistenthash", "1.0.0", "serendipity", "fst", 3000, false, false);
        DemoService demoService = rpcClient.create(DemoService.class);
        String result = demoService.hello("serendipity");
        logger.info("RpcConsumerNativeTest 获得的结果: {}", result);
        rpcClient.shutdown();
    }

    @Test
    public void testInterfaceRpc() {
        RpcClient rpcClient = new RpcClient("127.0.0.1:2181", "zookeeper","random", "1.0.0", "serendipity", "fst", 3000, false, false);
        DemoService demoService = rpcClient.create(DemoService.class);
        String result = demoService.hello("serendipity~ 同步方法~~");
        logger.info("testInterfaceRpc 获得的数据: {}", result);
        rpcClient.shutdown();
    }

    @Test
    public void testAsyncInterfaceRpc() throws Exception {
        RpcClient rpcClient = new RpcClient("127.0.0.1:2181", "zookeeper","random", "1.0.0", "serendipity", "fst", 3000, false, false);
        IAsyncObjectProxy demoService = rpcClient.createAsync(DemoService.class);
        RPCFuture future = demoService.call("hello", "serendipity~ 异步方法");
        logger.info("testAsyncInterfaceRpc 获得的数据: {}", future.get());
        rpcClient.shutdown();
    }

}
