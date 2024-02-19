package com.serendipity.rpc.test.spi.service.impl;

import com.serendipity.rpc.spi.annotation.SPIClass;
import com.serendipity.rpc.test.spi.service.SPIService;

/**
 * @author serendipity
 * @version 1.0
 * @date 2024/2/19
 **/
@SPIClass
public class SPIServiceImpl implements SPIService {
    @Override
    public String hello(String name) {

        System.out.println("你好啊~ "+ name);
        return "hello" + name;
    }
}
