package com.serendipity.rpc.consumer.common;

import com.serendipity.rpc.common.helper.RpcServiceHelper;
import com.serendipity.rpc.common.ip.IpUtils;
import com.serendipity.rpc.common.threadpoll.ClientThreadPool;
import com.serendipity.rpc.consumer.common.handler.RpcConsumerHandler;
import com.serendipity.rpc.consumer.common.helper.RpcConsumerHandlerHelper;
import com.serendipity.rpc.consumer.common.initializer.RpcConsumerInitializer;
import com.serendipity.rpc.consumer.common.manage.ConsumerConnectionManager;
import com.serendipity.rpc.protocol.RpcProtocol;
import com.serendipity.rpc.protocol.meta.ServiceMeta;
import com.serendipity.rpc.protocol.request.RpcRequest;
import com.serendipity.rpc.proxy.api.consumer.Consumer;
import com.serendipity.rpc.proxy.api.future.RPCFuture;
import com.serendipity.rpc.registry.api.RegistryService;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.*;

/**
 * 服务消费者
 *
 * @author serendipity
 * @version 1.0
 * @date 2024/2/16
 **/
public class RpcConsumer implements Consumer {

    private final Logger logger = LoggerFactory.getLogger(RpcConsumer.class);
    private final Bootstrap bootstrap;
    private final EventLoopGroup eventLoopGroup;
    private static volatile RpcConsumer instance;
    private final String localIp;

    /**
     * 定时任务发送心跳数据
     */
    private ScheduledExecutorService executorService;

    /**
     * 心跳间隔时间，默认30秒
     */
    private int heartbeatInterval = 30000;

    /**
     * 扫描并移除空闲连接时间，默认60秒
     */
    private int scanNotActiveChannelInterval = 60000;


    private RpcConsumer(int heartbeatInterval, int scanNotActiveChannelInterval) {
        if (heartbeatInterval > 0) {
            this.heartbeatInterval = heartbeatInterval;
        }
        if (scanNotActiveChannelInterval > 0) {
            this.scanNotActiveChannelInterval = scanNotActiveChannelInterval;
        }
        localIp = IpUtils.getLocalHostIp();
        bootstrap = new Bootstrap();
        eventLoopGroup = new NioEventLoopGroup(4);
        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(new RpcConsumerInitializer(heartbeatInterval));
        this.startHeartbeat();
    }

    private void startHeartbeat() {
        executorService = Executors.newScheduledThreadPool(2);

        // 扫描并处理所有不活跃的连接任务
        executorService.scheduleAtFixedRate(() -> {
            System.out.println("=============scanNotActiveChannel============");
            ConsumerConnectionManager.scanNotActiveChannel();
        }, 10, scanNotActiveChannelInterval, TimeUnit.MILLISECONDS);

        // 广播 Ping 消息任务
        executorService.scheduleAtFixedRate(() -> {
            System.out.println("=============broadcastPingMessageFromConsumer============");
            ConsumerConnectionManager.broadcastPingMessageFromConsumer();
        }, 3, heartbeatInterval, TimeUnit.MILLISECONDS);


    }

    /**
     * 单例模式
     */
    public static RpcConsumer getInstance(int heartbeatInterval, int scanNotActiveChannelInterval) {
        if (instance == null) {
            synchronized (RpcConsumer.class) {
                if (instance == null) {
                    instance = new RpcConsumer(heartbeatInterval, scanNotActiveChannelInterval);
                }
            }
        }
        return instance;
    }

    public void close() {
        RpcConsumerHandlerHelper.closeRpcClientHandler();
        eventLoopGroup.shutdownGracefully();
        ClientThreadPool.shutdown();
    }

    /**
     * 正常应是从 注册中心 获取服务，暂时写死直接
     *
     * @param protocol 发送的数据
     */
    @Override
    public RPCFuture sendRequest(RpcProtocol<RpcRequest> protocol, RegistryService registryService) throws Exception {
        RpcRequest request = protocol.getBody();
        String serviceKey = RpcServiceHelper.buildServiceKey(request.getClassName(), request.getVersion(), request.getGroup());
        Object[] params = request.getParameters();
        int invokerHashCode = (params == null || params.length <= 0) ? serviceKey.hashCode() : params[0].hashCode();
        ServiceMeta serviceMeta = registryService.discovery(serviceKey, invokerHashCode, localIp);

        if (serviceMeta != null) {
            RpcConsumerHandler handler = RpcConsumerHandlerHelper.get(serviceMeta);
            // 若缓存中不存在 处理器
            if (handler == null) {
                handler = getRpcConsumerHandler(serviceMeta.getServiceAddr(), serviceMeta.getServicePort());
                RpcConsumerHandlerHelper.put(serviceMeta, handler);
            } else if (!handler.getChannel().isActive()) {
                // 缓存中存在相应的 RpcConsumerHandler 但是不活跃
                handler.close();
                handler = getRpcConsumerHandler(serviceMeta.getServiceAddr(), serviceMeta.getServicePort());
                RpcConsumerHandlerHelper.put(serviceMeta, handler);
            }
            return handler.sendRequest(protocol, request.getAsync(), request.getOneway());
        }
        return null;
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
