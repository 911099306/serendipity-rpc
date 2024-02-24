package com.serendipity.rpc.ratelimiter.base;

import com.serendipity.rpc.ratelimiter.api.RateLimiterInvoker;

/**
 * @author serendipity
 * @version 1.0
 * @date 2024/2/25
 **/
public abstract class AbstractRateLimiterInvoker implements RateLimiterInvoker {
    /**
     * 在milliSeconds毫秒内最多能够通过的请求个数
     */
    protected int permits;
    /**
     * 毫秒数
     */
    protected int milliSeconds;

    @Override
    public void init(int permits, int milliSeconds) {
        this.permits = permits;
        this.milliSeconds = milliSeconds;
    }
}
