package com.serendipity.rpc.ratelimiter.guava;

import com.google.common.util.concurrent.RateLimiter;
import com.serendipity.rpc.ratelimiter.base.AbstractRateLimiterInvoker;
import com.serendipity.rpc.spi.annotation.SPIClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author serendipity
 * @version 1.0
 * @date 2024/2/25
 **/
@SPIClass
public class GuavaRateLimiterInvoker extends AbstractRateLimiterInvoker {

    private final Logger logger = LoggerFactory.getLogger(GuavaRateLimiterInvoker.class);
    private RateLimiter rateLimiter;

    @Override
    public boolean tryAcquire() {
        logger.info("execute guava rate limiter...");
        return this.rateLimiter.tryAcquire();
    }

    @Override
    public void release() {
        //TODO ignore
    }

    @Override
    public void init(int permits, int milliSeconds) {
        super.init(permits, milliSeconds);
        //转换成每秒钟最多允许的个数
        double permitsPerSecond = ((double) permits) / milliSeconds * 1000;
        this.rateLimiter = RateLimiter.create(permitsPerSecond);
    }
}
