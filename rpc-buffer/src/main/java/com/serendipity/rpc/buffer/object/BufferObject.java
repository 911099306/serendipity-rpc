package com.serendipity.rpc.buffer.object;

import com.serendipity.rpc.protocol.RpcProtocol;
import io.netty.channel.ChannelHandlerContext;

import java.io.Serializable;

/**
 * @author serendipity
 * @version 1.0
 * @date 2024/2/24
 **/
public class BufferObject <T> implements Serializable {
    private static final long serialVersionUID = -5465112244213170405L;

    /**
     * Netty读写数据的ChannelHandlerContext
     */
    private ChannelHandlerContext ctx;

    /**
     * 网络传输协议对象
     */
    private RpcProtocol<T> protocol;

    public BufferObject() {
    }

    public BufferObject(ChannelHandlerContext ctx, RpcProtocol<T> protocol) {
        this.ctx = ctx;
        this.protocol = protocol;
    }

    public ChannelHandlerContext getCtx() {
        return ctx;
    }

    public void setCtx(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }

    public RpcProtocol<T> getProtocol() {
        return protocol;
    }

    public void setProtocol(RpcProtocol<T> protocol) {
        this.protocol = protocol;
    }
}
