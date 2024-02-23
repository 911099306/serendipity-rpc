package com.serendipity.rpc.demo.provider.impl;

import com.serendipity.rpc.annotation.RpcService;
import com.serendipity.rpc.demo.api.DemoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author serendipity
 * @version 1.0
 * @date 2024/2/21
 **/
@RpcService(interfaceClass = DemoService.class, interfaceClassName = "com.serendipity.rpc.test.api.DemoService", version = "1.0.0", group = "serendipity", weight = 2)
public class ProviderDemoServiceImpl implements DemoService {
    private final Logger logger = LoggerFactory.getLogger(ProviderDemoServiceImpl.class);
    @Override
    public String hello(String name) {
        if ("serendipity".equals(name)) {
            throw new RuntimeException("Rpc 调用服务出现异常~~");
        }
        logger.info("调用hello方法传入的参数为===>>>{}", name);
        return "hello " + name;
    }
}
