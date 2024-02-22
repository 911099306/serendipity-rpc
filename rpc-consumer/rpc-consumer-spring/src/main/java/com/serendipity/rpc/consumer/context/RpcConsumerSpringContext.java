package com.serendipity.rpc.consumer.context;

import org.springframework.context.ApplicationContext;

/**
 * Spring 上下文
 *
 * @author serendipity
 * @version 1.0
 * @date 2024/2/23
 **/
public class RpcConsumerSpringContext {
    /**
     * Spring ApplicationContext
     */
    private ApplicationContext context;

    private RpcConsumerSpringContext(){

    }

    private static class Holder{
        private static final RpcConsumerSpringContext INSTANCE = new RpcConsumerSpringContext();
    }

    public static RpcConsumerSpringContext getInstance(){
        return Holder.INSTANCE;
    }

    public ApplicationContext getContext() {
        return context;
    }

    public void setContext(ApplicationContext context) {
        this.context = context;
    }
}
