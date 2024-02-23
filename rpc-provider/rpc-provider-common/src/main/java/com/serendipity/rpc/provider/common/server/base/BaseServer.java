package com.serendipity.rpc.provider.common.server.base;

import com.serendipity.rpc.codec.RpcDecoder;
import com.serendipity.rpc.codec.RpcEncoder;
import com.serendipity.rpc.constants.RpcConstants;
import com.serendipity.rpc.provider.common.handler.RpcProviderHandler;
import com.serendipity.rpc.provider.common.manager.ProviderConnectionManager;
import com.serendipity.rpc.provider.common.server.api.Server;
import com.serendipity.rpc.registry.api.RegistryService;
import com.serendipity.rpc.registry.api.config.RegistryConfig;
import com.serendipity.rpc.registry.zookeeper.ZookeeperRegistryService;
import com.serendipity.rpc.spi.loader.ExtensionLoader;
import io.netty.bootstrap.ServerBootstrap;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author serendipity
 * @version 1.0
 * @date 2024/2/5
 **/
public class BaseServer implements Server {
    private final Logger logger = LoggerFactory.getLogger(BaseServer.class);

    /**
     * 主机域名 或 IP地址
     */
    protected String host = "127.0.0.1";

    /**
     * 端口号
     */
    protected int port = 27110;

    protected String serverRegistryHost;
    protected int serverRegistryPort;

    /**
     * 实体类映射关系
     */
    protected Map<String, Object> handlerMap = new HashMap<>();

    /**
     * 代理类型
     */
    private String reflectType;

    /**
     * 注册中心服务
     */
    protected RegistryService registryService;

    /**
     * 心跳定时任务线程池
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

    /**
     * 结果缓存过期时长，默认5秒
     */
    private int resultCacheExpire = 5000;
    /**
     * 是否开启结果缓存
     */
    private boolean enableResultCache;

    /**
     * 核心线程数
     */
    private int corePoolSize;
    /**
     * 最大线程数
     */
    private int maximumPoolSize;

    public BaseServer(String serverAddress, String serverRegistryAddress, String registryAddress, String registryType, String registryLoadBalanceType, String reflectType, int heartbeatInterval, int scanNotActiveChannelInterval, boolean enableResultCache, int resultCacheExpire, int corePoolSize, int maximumPoolSize) {
        if (!StringUtils.isEmpty(serverAddress)) {
            String[] serverArray = serverAddress.split(":");
            this.host = serverArray[0];
            this.port = Integer.parseInt(serverArray[1]);
        }
        if (!StringUtils.isEmpty(serverRegistryAddress)) {
            String[] serverRegistryAddressArray = serverRegistryAddress.split(":");
            this.serverRegistryHost = serverRegistryAddressArray[0];
            this.serverRegistryPort = Integer.parseInt(serverRegistryAddressArray[1]);
        } else {
            this.serverRegistryHost = this.host;
            this.serverRegistryPort = this.port;
        }
        if (heartbeatInterval > 0) {
            this.heartbeatInterval = heartbeatInterval;
        }
        if (scanNotActiveChannelInterval > 0) {
            this.scanNotActiveChannelInterval = scanNotActiveChannelInterval;
        }
        this.reflectType = reflectType;
        this.registryService = this.getRegistryService(registryAddress, registryType, registryLoadBalanceType);

        if (resultCacheExpire > 0) {
            this.resultCacheExpire = resultCacheExpire;
        }
        this.enableResultCache = enableResultCache;
        this.corePoolSize = corePoolSize;
        this.maximumPoolSize = maximumPoolSize;
    }

    /**
     * 创建服务注册与发现的实现类
     *
     * @param registryAddress 注册中心地址
     * @param registryType    注册中心类型
     */
    private RegistryService getRegistryService(String registryAddress, String registryType, String registryLoadBalanceType) {
        RegistryService registryService = null;
        try {
            registryService = ExtensionLoader.getExtension(RegistryService.class, registryType);
            registryService.init(new RegistryConfig(registryAddress, registryType, registryLoadBalanceType));
        } catch (Exception e) {
            logger.error("RPC Server init error", e);
        }
        return registryService;
    }

    @Override
    public void startNettyServer() {
        this.startHeartbeat();
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel channel) throws Exception {
                            channel.pipeline()
                                    .addLast(RpcConstants.CODEC_DECODER, new RpcDecoder())
                                    .addLast(RpcConstants.CODEC_ENCODER, new RpcEncoder())
                                    // 读空闲时间、写空闲时间，读/写空闲时间 每这写时间间隔触发方法，检测是否发生过读/写时间，若没有，则触发超时事件：userEventTriggered（handler）进行关闭连接
                                    .addLast(RpcConstants.CODEC_SERVER_IDLE_HANDLER, new IdleStateHandler(0, 0, heartbeatInterval, TimeUnit.MILLISECONDS))
                                    .addLast(RpcConstants.CODEC_HANDLER, new RpcProviderHandler(reflectType, enableResultCache, resultCacheExpire,corePoolSize, maximumPoolSize, handlerMap));
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            ChannelFuture future = bootstrap.bind(host, port).sync();
            logger.info("Server started on {}:{}", host, port);
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            logger.error("RPC Server start error", e);
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }


    private void startHeartbeat() {
        executorService = Executors.newScheduledThreadPool(2);
        // 扫描并处理所有不活跃的连接
        executorService.scheduleAtFixedRate(() -> {
            // logger.info("=============scanNotActiveChannel============");
            ProviderConnectionManager.scanNotActiveChannel();
        }, 10, scanNotActiveChannelInterval, TimeUnit.MILLISECONDS);

        executorService.scheduleAtFixedRate(() -> {
            // logger.info("=============broadcastPingMessageFromProvoder============");
            ProviderConnectionManager.broadcastPingMessageFromProvider();
        }, 3, heartbeatInterval, TimeUnit.MILLISECONDS);
    }
}
