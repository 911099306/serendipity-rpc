package com.serendipity.rpc.demo.spring.annotation.consumer.service.impl;

import com.serendipity.rpc.annotation.RpcReference;
import com.serendipity.rpc.demo.api.DemoService;
import com.serendipity.rpc.demo.spring.annotation.consumer.service.ConsumerDemoService;
import org.springframework.stereotype.Service;

/**
 * @author serendipity
 * @version 1.0
 * @date 2024/2/22
 **/
@Service
public class ConsumerDemoServiceImpl implements ConsumerDemoService {

    @RpcReference(registryType = "zookeeper", registryAddress = "127.0.0.1:2181", loadBalanceType = "zkconsistenthash", version = "1.0.0", group = "binghe", serializationType = "protostuff", proxy = "cglib", timeout = 30000, async = false, oneway = false)
    private DemoService demoService;
    @Override
    public String hello(String name) {
        return demoService.hello(name);
    }
}
