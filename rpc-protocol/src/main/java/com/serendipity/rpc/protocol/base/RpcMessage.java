package com.serendipity.rpc.protocol.base;

/**
 * 协议：消息体
 *
 * @author serendipity
 * @version 1.0
 * @date 2024/2/5
 **/
public class RpcMessage {

    /**
     * 是否单向发送
     */
    private boolean oneway;

    /**
     * 是否异步调用
     */
    private boolean async;

    public boolean getOneway() {
        return oneway;
    }

    public void setOneway(boolean oneway) {
        this.oneway = oneway;
    }

    public boolean getAsync() {
        return async;
    }

    public void setAsync(boolean async) {
        this.async = async;
    }
}
