package com.serendipity.test.consumer;

import com.serendipity.rpc.consumer.RpcClient;
import com.serendipity.rpc.test.api.DemoService;
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
        RpcClient rpcClient = new RpcClient("1.0.0", "serendipity", "jdk", 3000, false, false);
        DemoService demoService = rpcClient.create(DemoService.class);
        String result = demoService.hello("serendipity");
        logger.info("RpcConsumerNativeTest 获得的结果: {}", result);
        rpcClient.shutdown();
    }
}
