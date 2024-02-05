package com.serendipity.rpc.common.id;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author serendipity
 * @version 1.0
 * @date 2024/2/5
 **/
public class IdFactory {
    private final static AtomicLong REQUEST_ID_GEN = new AtomicLong(0);

    public static Long getId(){
        return REQUEST_ID_GEN.incrementAndGet();
    }
}
