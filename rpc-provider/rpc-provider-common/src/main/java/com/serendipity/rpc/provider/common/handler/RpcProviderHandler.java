package com.serendipity.rpc.provider.common.handler;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.serendipity.rpc.buffer.cache.BufferCacheManager;
import com.serendipity.rpc.buffer.object.BufferObject;
import com.serendipity.rpc.cache.result.CacheResultKey;
import com.serendipity.rpc.cache.result.CacheResultManager;
import com.serendipity.rpc.common.helper.RpcServiceHelper;
import com.serendipity.rpc.common.threadpoll.ServerThreadPool;
import com.serendipity.rpc.connection.manager.ConnectionManager;
import com.serendipity.rpc.constants.RpcConstants;
import com.serendipity.rpc.protocol.RpcProtocol;
import com.serendipity.rpc.protocol.enumeration.RpcStatus;
import com.serendipity.rpc.protocol.enumeration.RpcType;
import com.serendipity.rpc.protocol.header.RpcHeader;
import com.serendipity.rpc.protocol.request.RpcRequest;
import com.serendipity.rpc.protocol.response.RpcResponse;
import com.serendipity.rpc.provider.common.cache.ProviderChannelCache;
import com.serendipity.rpc.reflect.api.ReflectInvoker;
import com.serendipity.rpc.spi.loader.ExtensionLoader;
import com.serendipity.rpc.threadpool.BufferCacheThreadPool;
import com.serendipity.rpc.threadpool.ConcurrentThreadPool;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.timeout.IdleStateEvent;
import net.sf.cglib.reflect.FastClass;
import net.sf.cglib.reflect.FastMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * @author serendipity
 * @version 1.0
 * @date 2024/2/5
 **/
public class RpcProviderHandler extends SimpleChannelInboundHandler<RpcProtocol<RpcRequest>> {

    private final Logger logger = LoggerFactory.getLogger(RpcProviderHandler.class);

    /**
     * 存储服务提供者中被@RpcService注解标注的类的对象
     * key为：serviceName#serviceVersion#group
     * value为：@RpcService注解标注的类的对象
     */
    private final Map<String, Object> handlerMap;

    /**
     * 调用采用哪种类型调用真实方法
     */
    private ReflectInvoker reflectInvoker;

    /**
     * 是否启用结果缓存
     */
    private final boolean enableResultCache;

    /**
     * 结果缓存管理器
     */
    private final CacheResultManager<RpcProtocol<RpcResponse>> cacheResultManager;

    /**
     * 线程池
     */
    private final ConcurrentThreadPool concurrentThreadPool;

    /**
     * 连接管理器
     */
    private ConnectionManager connectionManager;

    /**
     * 是否开启缓冲区
     */
    private boolean enableBuffer;

    /**
     * 缓冲区管理器
     */
    private BufferCacheManager<BufferObject<RpcRequest>> bufferCacheManager;

    public RpcProviderHandler(String reflectType, boolean enableResultCache, int resultCacheExpire, int corePoolSize, int maximumPoolSize, int maxConnections, String disuseStrategyType, boolean enableBuffer, int bufferSize, Map<String, Object> handlerMap) {
        this.handlerMap = handlerMap;
        this.reflectInvoker = ExtensionLoader.getExtension(ReflectInvoker.class, reflectType);
        this.enableResultCache = enableResultCache;
        if (resultCacheExpire <= 0) {
            resultCacheExpire = RpcConstants.RPC_SCAN_RESULT_CACHE_EXPIRE;
        }
        this.cacheResultManager = CacheResultManager.getInstance(resultCacheExpire, enableResultCache);
        this.concurrentThreadPool = ConcurrentThreadPool.getInstance(corePoolSize, maximumPoolSize);
        this.connectionManager = ConnectionManager.getInstance(maxConnections, disuseStrategyType);
        this.enableBuffer = enableBuffer;
        //开启缓冲
        if (enableBuffer){
            logger.info("enable buffer...");
            bufferCacheManager = BufferCacheManager.getInstance(bufferSize);
            BufferCacheThreadPool.submit(() -> {
                consumerBufferCache();
            });
        }
    }

    /**
     * 消费缓冲区的数据
     */
    private void consumerBufferCache(){
        //不断消息缓冲区的数据
        while (true){
            BufferObject<RpcRequest> bufferObject = this.bufferCacheManager.take();
            if (bufferObject != null){
                ChannelHandlerContext ctx = bufferObject.getCtx();
                RpcProtocol<RpcRequest> protocol = bufferObject.getProtocol();
                RpcHeader header = protocol.getHeader();
                RpcProtocol<RpcResponse> responseRpcProtocol = handlerRequestMessageWithCache(protocol, header);
                this.writeAndFlush(header.getRequestId(), ctx, responseRpcProtocol);
            }
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        ProviderChannelCache.add(ctx.channel());
        connectionManager.add(ctx.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        ProviderChannelCache.remove(ctx.channel());
        connectionManager.remove(ctx.channel());
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        super.channelUnregistered(ctx);
        ProviderChannelCache.remove(ctx.channel());
        connectionManager.remove(ctx.channel());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcProtocol<RpcRequest> protocol) throws Exception {

        concurrentThreadPool.submit(() -> {
            connectionManager.update(ctx.channel());
            if (enableBuffer){  //开启队列缓冲
                this.bufferRequest(ctx, protocol);
            }else{  //未开启队列缓冲
                this.submitRequest(ctx, protocol);
            }
        });
    }

    /**
     * 缓冲数据
     */
    private void bufferRequest(ChannelHandlerContext ctx, RpcProtocol<RpcRequest> protocol) {
        RpcHeader header = protocol.getHeader();
        //接收到服务消费者发送的心跳消息
        if (header.getMsgType() == (byte) RpcType.HEARTBEAT_FROM_CONSUMER.getType()){
            RpcProtocol<RpcResponse> responseRpcProtocol = handlerHeartbeatMessageFromConsumer(protocol, header);
            this.writeAndFlush(protocol.getHeader().getRequestId(), ctx, responseRpcProtocol);
        }else if (header.getMsgType() == (byte) RpcType.HEARTBEAT_TO_PROVIDER.getType()){  //接收到服务消费者响应的心跳消息
            handlerHeartbeatMessageToProvider(protocol, ctx.channel());
        }else if (header.getMsgType() == (byte) RpcType.REQUEST.getType()){ //请求消息
            this.bufferCacheManager.put(new BufferObject<>(ctx, protocol));
        }
    }

    /**
     * 提交请求
     */
    private void submitRequest(ChannelHandlerContext ctx, RpcProtocol<RpcRequest> protocol) {
        RpcProtocol<RpcResponse> responseRpcProtocol = handlerMessage(protocol, ctx.channel());
        writeAndFlush(protocol.getHeader().getRequestId(), ctx, responseRpcProtocol);
    }

    /**
     * 向服务消费者写回数据
     */
    private void writeAndFlush(long requestId, ChannelHandlerContext ctx,  RpcProtocol<RpcResponse> responseRpcProtocol){
        ctx.writeAndFlush(responseRpcProtocol).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                logger.debug("Send response for request " + requestId);
            }
        });
    }

    /**
     * 解析请求消息协议，判断是心跳数据包 还是 普通request信息
     *
     * @param protocol 协议内容
     * @return 响应结果
     */
    private RpcProtocol<RpcResponse> handlerMessage(RpcProtocol<RpcRequest> protocol, Channel channel) {
        RpcProtocol<RpcResponse> responseRpcProtocol = null;
        RpcHeader header = protocol.getHeader();
        // 根据消息类型，进行不同的处理
        if (header.getMsgType() == (byte) RpcType.HEARTBEAT_FROM_CONSUMER.getType()) {
            responseRpcProtocol = handlerHeartbeatMessageFromConsumer(protocol, header);
        } else if (header.getMsgType() == (byte) RpcType.HEARTBEAT_TO_PROVIDER.getType()) {  // 接收到服务消费者响应的心跳消息
            handlerHeartbeatMessageToProvider(protocol, channel);
        } else if (header.getMsgType() == (byte) RpcType.REQUEST.getType()) {
            responseRpcProtocol = handlerRequestMessageWithCache(protocol, header);
        }
        return responseRpcProtocol;
    }


    /**
     * 结合缓存处理结果
     */
    private RpcProtocol<RpcResponse> handlerRequestMessageWithCache(RpcProtocol<RpcRequest> protocol, RpcHeader header) {
        header.setMsgType((byte) RpcType.RESPONSE.getType());
        if (enableResultCache) return handlerRequestMessageCache(protocol, header);
        return handlerRequestMessage(protocol, header);
    }

    /**
     * 处理缓存
     *
     * @param protocol 协议
     * @param header   消息头
     * @return 结果
     */
    private RpcProtocol<RpcResponse> handlerRequestMessageCache(RpcProtocol<RpcRequest> protocol, RpcHeader header) {
        RpcRequest request = protocol.getBody();
        CacheResultKey cacheKey = new CacheResultKey(request.getClassName(), request.getMethodName(), request.getParameterTypes(), request.getParameters(), request.getVersion(), request.getGroup());
        RpcProtocol<RpcResponse> responseRpcProtocol = cacheResultManager.get(cacheKey);
        if (responseRpcProtocol == null) {
            responseRpcProtocol = handlerRequestMessage(protocol, header);
            // 设置保存的时间
            cacheKey.setCacheTimeStamp(System.currentTimeMillis());
            cacheResultManager.put(cacheKey, responseRpcProtocol);
        }
        responseRpcProtocol.setHeader(header);
        return responseRpcProtocol;
    }


    /**
     * 处理服务消费者响应的心跳消息
     */
    private void handlerHeartbeatMessageToProvider(RpcProtocol<RpcRequest> protocol, Channel channel) {
        logger.info("receive service consumer heartbeat message, the consumer is: {}, the heartbeat message is: {}", channel.remoteAddress(), protocol.getBody().getParameters()[0]);
    }

    /**
     * 处理心跳数据
     *
     * @param protocol 协议数据
     * @param header   消息头
     * @return 响应结果
     */
    private RpcProtocol<RpcResponse> handlerHeartbeatMessageFromConsumer(RpcProtocol<RpcRequest> protocol, RpcHeader header) {
        header.setMsgType((byte) RpcType.HEARTBEAT_TO_CONSUMER.getType());
        RpcRequest request = protocol.getBody();
        RpcProtocol<RpcResponse> responseRpcProtocol = new RpcProtocol<RpcResponse>();
        RpcResponse response = new RpcResponse();
        response.setResult(RpcConstants.HEARTBEAT_PONG);
        response.setAsync(request.getAsync());
        response.setOneway(request.getOneway());
        header.setStatus((byte) RpcStatus.SUCCESS.getCode());
        responseRpcProtocol.setHeader(header);
        responseRpcProtocol.setBody(response);
        return responseRpcProtocol;
    }

    /**
     * 处理请求消息
     *
     * @param protocol 协议信息
     * @param header   协议头
     * @return 响应结果
     */
    private RpcProtocol<RpcResponse> handlerRequestMessage(RpcProtocol<RpcRequest> protocol, RpcHeader header) {
        RpcRequest request = protocol.getBody();
        logger.debug("Receive request " + header.getRequestId());
        RpcProtocol<RpcResponse> responseRpcProtocol = new RpcProtocol<RpcResponse>();
        RpcResponse response = new RpcResponse();
        try {
            Object result = handle(request);
            response.setResult(result);
            response.setAsync(request.getAsync());
            response.setOneway(request.getOneway());
            header.setStatus((byte) RpcStatus.SUCCESS.getCode());
        } catch (Throwable t) {
            response.setError(t.toString());
            header.setStatus((byte) RpcStatus.FAIL.getCode());
            logger.error("RPC Server handle request error", t);
        }
        responseRpcProtocol.setHeader(header);
        responseRpcProtocol.setBody(response);
        return responseRpcProtocol;
    }

    /**
     * 解析消息请求，并通过反射调用请求服务返回响应结果
     *
     * @param request 请求信息
     * @return 请求服务响应结果
     * @throws Throwable 抛出异常
     */
    private Object handle(RpcRequest request) throws Throwable {
        String serviceKey = RpcServiceHelper.buildServiceKey(request.getClassName(), request.getVersion(), request.getGroup());
        Object serviceBean = handlerMap.get(serviceKey);
        if (serviceBean == null) {
            throw new RuntimeException(String.format("service not exist: %s:%s", request.getClassName(), request.getMethodName()));
        }

        Class<?> serviceClass = serviceBean.getClass();
        String methodName = request.getMethodName();
        Class<?>[] parameterTypes = request.getParameterTypes();
        Object[] parameters = request.getParameters();

        logger.debug(serviceClass.getName());
        logger.debug(methodName);
        if (parameterTypes != null && parameterTypes.length > 0) {
            for (int i = 0; i < parameterTypes.length; ++i) {
                logger.debug(parameterTypes[i].getName());
            }
        }

        if (parameters != null && parameters.length > 0) {
            for (int i = 0; i < parameters.length; ++i) {
                logger.debug(parameters[i].toString());
            }
        }
        return this.reflectInvoker.invokeMethod(serviceBean, serviceClass, methodName, parameterTypes, parameters);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("server caught exception", cause);
        connectionManager.remove(ctx.channel());
        ctx.close();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        // 如果是IdleStateEvent事件
        if (evt instanceof IdleStateEvent) {
            Channel channel = ctx.channel();
            try {
                logger.info("IdleStateEvent triggered, close channel " + channel.remoteAddress());
                connectionManager.remove(channel);
                channel.close();
            } finally {
                channel.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
            }
        }
        super.userEventTriggered(ctx, evt);
    }
}
