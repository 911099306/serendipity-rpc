package com.serendipity.rpc.protocol;

import com.serendipity.rpc.protocol.header.RpcHeader;

import java.io.Serializable;

/**
 * 自定义 RPC 协议
 *
 * @author serendipity
 * @version 1.0
 * @date 2024/2/5
 **/
public class RpcProtocol<T> implements Serializable {
    private static final long serialVersionUID = 292789485166173277L;
    /**
     * 消息头
     */
    private RpcHeader header;
    /**
     * 消息体
     */
    private T body;

    public RpcHeader getHeader() {
        return header;
    }

    public void setHeader(RpcHeader header) {
        this.header = header;
    }

    public T getBody() {
        return body;
    }

    public void setBody(T body) {
        this.body = body;
    }

}
