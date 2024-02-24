package com.serendipity.rpc.ratelimiter.api;

import com.serendipity.rpc.constants.RpcConstants;
import com.serendipity.rpc.spi.annotation.SPI;

/**
 * 限流调用器SPI，秒级单位限流
 *
 * @author serendipity
 * @version 1.0
 * @date 2024/2/25
 **/
@SPI(RpcConstants.DEFAULT_RATELIMITER_INVOKER)
public interface RateLimiterInvoker {

    /**
     * 限流方法
     */
    boolean tryAcquire();

    /**
     * 释放资源
     */
    void release();

    /**
     * 在milliSeconds毫秒内最多允许通过permits个请求
     * @param permits 在milliSeconds毫秒内最多能够通过的请求个数
     * @param milliSeconds 毫秒数
     */
    default void init(int permits, int milliSeconds){}
}
