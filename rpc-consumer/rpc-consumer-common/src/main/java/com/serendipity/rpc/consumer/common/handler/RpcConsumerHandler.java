package com.serendipity.rpc.consumer.common.handler;

import com.alibaba.fastjson2.JSONObject;
import com.serendipity.rpc.consumer.common.context.RpcContext;
import com.serendipity.rpc.protocol.RpcProtocol;
import com.serendipity.rpc.protocol.header.RpcHeader;
import com.serendipity.rpc.protocol.request.RpcRequest;
import com.serendipity.rpc.protocol.response.RpcResponse;
import com.serendipity.rpc.proxy.api.future.RPCFuture;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.SocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author serendipity
 * @version 1.0
 * @date 2024/2/16
 **/
public class RpcConsumerHandler extends SimpleChannelInboundHandler<RpcProtocol<RpcResponse>> {

    private final Logger logger = LoggerFactory.getLogger(RpcConsumerHandler.class);

    private volatile Channel channel;
    private SocketAddress remotePeer;

    /**
     * 存储请求ID与RpcResponse协议的映射关系
     */
    // private Map<Long, RpcProtocol<RpcResponse>> pendingResponse = new ConcurrentHashMap<>();
    private Map<Long, RPCFuture> pendingRPC = new ConcurrentHashMap<>();

    public Channel getChannel() {
        return channel;
    }

    public SocketAddress getRemotePeer() {
        return remotePeer;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        this.remotePeer = this.channel.remoteAddress();
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        this.channel = ctx.channel();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcProtocol<RpcResponse> protocol) throws Exception {
        if (protocol == null) {
            logger.info("服务消费者接收到的数据 为空null ~~~");
            return;
        }
        logger.info("服务消费者接收到的数据===>>>{}", JSONObject.toJSONString(protocol));
        RpcHeader header = protocol.getHeader();
        long requestId = header.getRequestId();
        // pendingResponse.put(requestId, protocol);
        RPCFuture rpcFuture = pendingRPC.remove(requestId);
        if (rpcFuture != null) {
            // 接收到 提供者 相应的数据， 将其交予 Future
            rpcFuture.done(protocol);
        }
    }

    /**
     * 服务消费者向服务提供者发送请求
     */
    public RPCFuture sendRequest(RpcProtocol<RpcRequest> protocol, boolean async, boolean oneway) {
        logger.info("服务消费者发送的数据===>>>{}", JSONObject.toJSONString(protocol));

        // 3. 同步、 异步、 单向
        return oneway ? this.sendRequestOneway(protocol) : async ? this.sendRequestAsync(protocol) : this.sendRequestSync(protocol);


        // 2. Future 代替 while
        // RPCFuture rpcFuture = this.getRpcFuture(protocol);
        // channel.writeAndFlush(protocol);
        // return rpcFuture;

        // 1. 原始模式 while 循环
        // channel.writeAndFlush(protocol);
        // RpcHeader header = protocol.getHeader();
        // long requestId = header.getRequestId();
        // 异步转同步
        // while (true) {
        //     RpcProtocol<RpcResponse> responseRpcProtocol = pendingResponse.remove(requestId);
        //     if (responseRpcProtocol != null) {
        //         return responseRpcProtocol.getBody().getResult();
        //     }
        // }
    }

    /**
     * 同步调用
     */
    private RPCFuture sendRequestSync(RpcProtocol<RpcRequest> protocol) {
        RPCFuture rpcFuture = this.getRpcFuture(protocol);
        channel.writeAndFlush(protocol);
        return rpcFuture;
    }

    /**
     * 异步调用
     */
    private RPCFuture sendRequestAsync(RpcProtocol<RpcRequest> protocol) {
        RPCFuture rpcFuture = this.getRpcFuture(protocol);
        // 如果是异步调用，则将RPCFuture放入RpcContext
        RpcContext.getContext().setRPCFUture(rpcFuture);
        channel.writeAndFlush(protocol);
        return null;
    }

    /**
     * 单向调用
     */
    private RPCFuture sendRequestOneway(RpcProtocol<RpcRequest> protocol) {
        channel.writeAndFlush(protocol);
        return null;
    }

    private RPCFuture getRpcFuture(RpcProtocol<RpcRequest> protocol) {
        RPCFuture rpcFuture = new RPCFuture(protocol);
        RpcHeader header = protocol.getHeader();
        long requestId = header.getRequestId();
        pendingRPC.put(requestId, rpcFuture);
        return rpcFuture;
    }

    public void close() {
        channel.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
    }
}
