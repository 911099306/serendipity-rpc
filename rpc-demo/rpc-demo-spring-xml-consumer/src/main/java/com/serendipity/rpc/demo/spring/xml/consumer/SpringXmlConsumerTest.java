package com.serendipity.rpc.demo.spring.xml.consumer;

import com.serendipity.rpc.consumer.RpcClient;
import com.serendipity.rpc.demo.api.DemoService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author serendipity
 * @version 1.0
 * @date 2024/2/22
 **/

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:client-spring.xml")
public class SpringXmlConsumerTest {

    private static Logger logger = LoggerFactory.getLogger(SpringXmlConsumerTest.class);

    @Autowired
    private RpcClient rpcClient;

    @Test
    public void testInterfaceRpc() throws InterruptedException {
        DemoService demoService = rpcClient.create(DemoService.class);
        String result = demoService.hello("serendipity~~~~~~~~~");
        logger.info("返回的结果数据===>>> " + result);
        //rpcClient.shutdown();
        while (true){
            Thread.sleep(1000);
        }
    }
}
