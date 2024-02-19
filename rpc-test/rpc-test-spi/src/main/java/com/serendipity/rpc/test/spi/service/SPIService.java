package com.serendipity.rpc.test.spi.service;

import com.serendipity.rpc.spi.annotation.SPI;

/**
 * @author serendipity
 * @version 1.0
 * @date 2024/2/19
 **/
@SPI("spiService")
public interface SPIService {

    String hello(String name);
}
