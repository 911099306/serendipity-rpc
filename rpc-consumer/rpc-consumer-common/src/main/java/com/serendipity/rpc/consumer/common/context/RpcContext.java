package com.serendipity.rpc.consumer.common.context;


import com.serendipity.rpc.proxy.api.future.RPCFuture;

/**
 * 保存 RPC 上下文
 *
 * @author serendipity
 * @version 1.0
 * @date 2024/2/17
 **/
public class RpcContext {
    private RpcContext(){
    }

    /**
     * RpcContext 实例
     */
    private static final RpcContext AGENT = new RpcContext();

    /**
     * 存放RPCFuture的InheritableThreadLocal
     */
    private static final InheritableThreadLocal<RPCFuture> RPC_FUTURE_INHERITABLE_THREAD_LOCAL = new InheritableThreadLocal<>();

    /**
     * 获取上下文
     */
    public static RpcContext getContext() {
        return AGENT;
    }

    /**
     * 获取 RPCFuture 保存到线程的上下文
     */
    public void setRPCFuture(RPCFuture rpcFuture) {
        RPC_FUTURE_INHERITABLE_THREAD_LOCAL.set(rpcFuture);
    }

    /**
     * 获取 RPCFuture
     */
    public RPCFuture getRPCFuture() {
        return RPC_FUTURE_INHERITABLE_THREAD_LOCAL.get();
    }

    /**
     * 移除 RPCFuture
     */
    public void removeRPCFuture() {
        RPC_FUTURE_INHERITABLE_THREAD_LOCAL.remove();
    }

}
