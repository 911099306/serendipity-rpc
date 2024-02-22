package com.serendipity.rpc.demo.spring.annotation.provider.impl;

import com.serendipity.rpc.annotation.RpcService;
import com.serendipity.rpc.demo.api.DemoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author serendipity
 * @version 1.0
 * @date 2024/2/22
 **/
@RpcService(interfaceClass = DemoService.class, interfaceClassName = "com.serendipity.rpc.demo.api.DemoService", version = "1.0.0", group = "serendipity", weight = 2)
public class ProviderDemoServiceImpl implements DemoService {

    private final Logger logger = LoggerFactory.getLogger(ProviderDemoServiceImpl.class);
    @Override
    public String hello(String name) {
        logger.info("调用hello方法传入的参数为===>>>{}", name);
        return "hello " + name;
    }
}
