package com.serendipity.rpc.test.provider.service.impl;

import com.serendipity.rpc.annotation.RpcService;
import com.serendipity.rpc.test.api.DemoService ;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author serendipity
 * @version 1.0
 * @date 2024/2/5
 **/
@RpcService(interfaceClass = DemoService.class, interfaceClassName = "com.serendipity.rpc.test.scanner.service.DemoService", version = "1.0.0", group = "serendipity")
public class ProviderDemoServiceImpl implements DemoService {

    private final Logger logger = LoggerFactory.getLogger(ProviderDemoServiceImpl.class);
    @Override
    public String hello(String name) {
        logger.info("调用hello方法传入的参数为===>>>{}", name);
        return "hello " + name;
    }
}
