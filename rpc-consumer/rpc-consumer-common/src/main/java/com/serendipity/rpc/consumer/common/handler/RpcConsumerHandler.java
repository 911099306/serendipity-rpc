package com.serendipity.rpc.consumer.common.handler;

import com.alibaba.fastjson2.JSONObject;
import com.serendipity.rpc.buffer.cache.BufferCacheManager;
import com.serendipity.rpc.constants.RpcConstants;
import com.serendipity.rpc.consumer.common.cache.ConsumerChannelCache;
import com.serendipity.rpc.consumer.common.context.RpcContext;
import com.serendipity.rpc.protocol.RpcProtocol;
import com.serendipity.rpc.protocol.enumeration.RpcStatus;
import com.serendipity.rpc.protocol.enumeration.RpcType;
import com.serendipity.rpc.protocol.header.RpcHeader;
import com.serendipity.rpc.protocol.header.RpcHeaderFactory;
import com.serendipity.rpc.protocol.request.RpcRequest;
import com.serendipity.rpc.protocol.response.RpcResponse;
import com.serendipity.rpc.proxy.api.consumer.Consumer;
import com.serendipity.rpc.proxy.api.future.RPCFuture;
import com.serendipity.rpc.threadpool.BufferCacheThreadPool;
import com.serendipity.rpc.threadpool.ConcurrentThreadPool;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;
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

    // 存储请求ID与RpcResponse协议的映射关系
    private Map<Long, RPCFuture> pendingRPC = new ConcurrentHashMap<>();

    private ConcurrentThreadPool concurrentThreadPool;

    /**
     * 是否开启缓冲区
     */
    private boolean enableBuffer;

    /**
     * 缓冲区管理器
     */
    private BufferCacheManager<RpcProtocol<RpcResponse>> bufferCacheManager;


    public RpcConsumerHandler(boolean enableBuffer, int bufferSize, ConcurrentThreadPool concurrentThreadPool) {
        this.concurrentThreadPool = concurrentThreadPool;
        this.enableBuffer = enableBuffer;
        if (enableBuffer){
            this.bufferCacheManager = BufferCacheManager.getInstance(bufferSize);
            BufferCacheThreadPool.submit(() -> {
                consumerBufferCache();
            });
        }
    }

    /**
     * 消费缓冲区数据
     */
    private void consumerBufferCache() {
        //不断消息缓冲区的数据
        while (true){
            RpcProtocol<RpcResponse> protocol = this.bufferCacheManager.take();
            if (protocol != null){
                this.handlerResponseMessage(protocol);
            }
        }
    }

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
        ConsumerChannelCache.add(channel);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            // 发送一次心跳数据
            RpcHeader header = RpcHeaderFactory.getRequestHeader(RpcConstants.SERIALIZATION_PROTOSTUFF, RpcType.HEARTBEAT_FROM_CONSUMER.getType());
            RpcProtocol<RpcRequest> requestRpcProtocol = new RpcProtocol<RpcRequest>();
            RpcRequest rpcRequest = new RpcRequest();
            rpcRequest.setParameters(new Object[]{RpcConstants.HEARTBEAT_PING});
            requestRpcProtocol.setHeader(header);
            requestRpcProtocol.setBody(rpcRequest);
            ctx.writeAndFlush(requestRpcProtocol);
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        this.channel = ctx.channel();
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        super.channelUnregistered(ctx);
        ConsumerChannelCache.remove(ctx.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        ConsumerChannelCache.remove(ctx.channel());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcProtocol<RpcResponse> protocol) throws Exception {
        if (protocol == null) {
            return;
        }
        concurrentThreadPool.submit(() -> {
            this.handlerMessage(protocol, ctx.channel());
        });
    }

    private void handlerMessage(RpcProtocol<RpcResponse> protocol, Channel channel) {
        RpcHeader header = protocol.getHeader();
        // 服务提供者响应的心跳消息
        if (header.getMsgType() == (byte) RpcType.HEARTBEAT_TO_CONSUMER.getType()) {
            this.handlerHeartbeatMessageToConsumer(protocol, channel);
        } else if (header.getMsgType() == (byte) RpcType.HEARTBEAT_FROM_PROVIDER.getType()) {
            this.handlerHeartbeatMessageFromProvider(protocol, channel);
        } else if (header.getMsgType() == (byte) RpcType.RESPONSE.getType()) { // 响应消息
            this.handlerResponseMessageOrBuffer(protocol);
        }
    }

    /**
     * 包含是否开启了缓冲区的响应消息
     */
    private void handlerResponseMessageOrBuffer(RpcProtocol<RpcResponse> protocol){
        if (enableBuffer){
            logger.info("enable buffer...");
            this.bufferCacheManager.put(protocol);
        }else {
            this.handlerResponseMessage(protocol);
        }
    }

    /**
     * 处理从服务提供者发送过来的心跳消息
     */
    private void handlerHeartbeatMessageFromProvider(RpcProtocol<RpcResponse> protocol, Channel channel) {
        RpcHeader header = protocol.getHeader();
        header.setMsgType((byte) RpcType.HEARTBEAT_TO_PROVIDER.getType());
        RpcProtocol<RpcRequest> requestRpcProtocol = new RpcProtocol<RpcRequest>();
        RpcRequest request = new RpcRequest();
        request.setParameters(new Object[]{RpcConstants.HEARTBEAT_PONG});
        header.setStatus((byte) RpcStatus.SUCCESS.getCode());
        requestRpcProtocol.setHeader(header);
        requestRpcProtocol.setBody(request);
        channel.writeAndFlush(requestRpcProtocol);
    }

    /**
     * 处理心跳消息
     */
    private void handlerHeartbeatMessageToConsumer(RpcProtocol<RpcResponse> protocol, Channel channel) {
        // 此处简单打印即可,实际场景可不做处理
        logger.info("receive service provider heartbeat message, the provider is: {}, the heartbeat message is: {}", channel.remoteAddress(), protocol.getBody().getResult());
    }

    /**
     * 处理响应消息
     */
    /**
     * 处理响应消息
     */
    private void handlerResponseMessage(RpcProtocol<RpcResponse> protocol) {
        long requestId = protocol.getHeader().getRequestId();
        RPCFuture rpcFuture = pendingRPC.remove(requestId);
        if (rpcFuture != null){
            rpcFuture.done(protocol);
        }
    }

    /**
     * 服务消费者向服务提供者发送请求
     */
    public RPCFuture sendRequest(RpcProtocol<RpcRequest> protocol, boolean async, boolean oneway) {
        logger.info("服务消费者发送的数据===>>>{}", JSONObject.toJSONString(protocol));
        return concurrentThreadPool.submit(() -> {
            return oneway ? this.sendRequestOneway(protocol) : async ? sendRequestAsync(protocol) : this.sendRequestSync(protocol);
        });
    }


    private RPCFuture sendRequestSync(RpcProtocol<RpcRequest> protocol) {
        RPCFuture rpcFuture = this.getRpcFuture(protocol);
        channel.writeAndFlush(protocol);
        return rpcFuture;
    }


    private RPCFuture sendRequestAsync(RpcProtocol<RpcRequest> protocol) {
        RPCFuture rpcFuture = this.getRpcFuture(protocol);
        // 如果是异步调用，则将RPCFuture放入RpcContext
        RpcContext.getContext().setRPCFuture(rpcFuture);
        channel.writeAndFlush(protocol);
        return null;
    }

    private RPCFuture sendRequestOneway(RpcProtocol<RpcRequest> protocol) {
        channel.writeAndFlush(protocol);
        return null;
    }


    private RPCFuture getRpcFuture(RpcProtocol<RpcRequest> protocol) {
        RPCFuture rpcFuture = new RPCFuture(protocol, concurrentThreadPool);
        RpcHeader header = protocol.getHeader();
        long requestId = header.getRequestId();
        pendingRPC.put(requestId, rpcFuture);
        return rpcFuture;
    }

    public void close() {
        channel.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
        ConsumerChannelCache.remove(channel);
    }
}
