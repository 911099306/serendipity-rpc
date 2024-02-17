package com.serendipity.rpc.consumer.common;

import com.serendipity.rpc.consumer.common.handler.RpcConsumerHandler;
import com.serendipity.rpc.consumer.common.initializer.RpcConsumerInitializer;
import com.serendipity.rpc.protocol.RpcProtocol;
import com.serendipity.rpc.protocol.request.RpcRequest;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 服务消费者
 *
 * @author serendipity
 * @version 1.0
 * @date 2024/2/16
 **/
public class RpcConsumer {

    private final Logger logger = LoggerFactory.getLogger(RpcConsumer.class);
    private final Bootstrap bootstrap;
    private final EventLoopGroup eventLoopGroup;

    private static volatile RpcConsumer instance;
    private static Map<String, RpcConsumerHandler> handlerMap = new ConcurrentHashMap<>();

    private RpcConsumer(){
        bootstrap = new Bootstrap();
        eventLoopGroup = new NioEventLoopGroup(4);
        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(new RpcConsumerInitializer());
    }

    /**
     * 单例模式
     */
    public static RpcConsumer getInstance() {
        if (instance == null) {
            synchronized (RpcConsumer.class) {
                if (instance == null) {
                    instance = new RpcConsumer();
                }
            }
        }
        return instance;
    }

    public void close() {
        eventLoopGroup.shutdownGracefully();
    }

    /**
     * 正常应是从 注册中心 获取服务，暂时写死直接
     * @param protocol 发送的数据
     */
    public void sendRequest(RpcProtocol<RpcRequest> protocol) throws Exception {
        String serviceAddress = "127.0.0.1";
        int port = 27880;
        String key = serviceAddress.concat("_").concat(String.valueOf(port));
        RpcConsumerHandler handler = handlerMap.get(key);

        // 缓存中 无 相应的处理器
        if (handler == null) {
            handler = getRpcConsumerHandler(serviceAddress, port);
            handlerMap.put(key, handler);
        } else if (!handler.getChannel().isActive()) {
            // 缓存中存在相应的 RpcConsumerHandler 但是不活跃
            handler.close();
            handler = getRpcConsumerHandler(serviceAddress, port);
            handlerMap.put(key, handler);
        }
        handler.sendRequest(protocol);

    }

    private RpcConsumerHandler getRpcConsumerHandler(String serviceAddress, int port) throws InterruptedException {
        ChannelFuture channelFuture = bootstrap.connect(serviceAddress, port).sync();
        channelFuture.addListener((ChannelFutureListener) listener -> {
            if (channelFuture.isSuccess()) {
                logger.info("connect rpc server {} on port {} success.", serviceAddress, port);
            } else {
                logger.error("connect rpc server {} on port {} failed.", serviceAddress, port);
                channelFuture.cause().printStackTrace();
                eventLoopGroup.shutdownGracefully();
            }
        });
        return channelFuture.channel().pipeline().get(RpcConsumerHandler.class);
    }
}
