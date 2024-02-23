package com.serendipity.demo.consumer.hello;

import com.serendipity.rpc.demo.api.DemoService;

/**
 * helloService容错服务实现类
 *
 * @author serendipity
 * @version 1.0
 * @date 2024/2/24
 **/
public class FallbackDemoServiceImpl implements DemoService {
    @Override
    public String hello(String name) {
        return "fallback hello " + name;
    }
}
