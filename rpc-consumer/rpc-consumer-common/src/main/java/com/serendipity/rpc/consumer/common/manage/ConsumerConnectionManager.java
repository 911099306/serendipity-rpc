package com.serendipity.rpc.consumer.common.manage;

import com.serendipity.rpc.constants.RpcConstants;
import com.serendipity.rpc.consumer.common.cache.ConsumerChannelCache;
import com.serendipity.rpc.protocol.RpcProtocol;
import com.serendipity.rpc.protocol.enumeration.RpcType;
import com.serendipity.rpc.protocol.header.RpcHeader;
import com.serendipity.rpc.protocol.header.RpcHeaderFactory;
import com.serendipity.rpc.protocol.request.RpcRequest;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.Set;
import java.util.stream.Collectors;

/**
 * 服务消费者连接管理器
 *
 * @author serendipity
 * @version 1.0
 * @date 2024/2/21
 **/
public class ConsumerConnectionManager {
    private static final Logger logger = LoggerFactory.getLogger(ConsumerConnectionManager.class);

    /**
     * 扫描并移除不活跃的连接
     */
    public static void scanNotActiveChannel() {
        Set<Channel> channelCache = ConsumerChannelCache.getChannelCache();
        if (channelCache == null || channelCache.isEmpty()) return;
        channelCache.stream().forEach(channel -> {
            if (!channel.isOpen() || !channel.isActive()) {
                channel.close();
                ConsumerChannelCache.remove(channel);
            }
        });
    }

    /**
     * 发送 ping 消息
     */
    public static void broadcastPingMessageFromConsumer() {
        Set<Channel> channelCache = ConsumerChannelCache.getChannelCache();
        if (channelCache == null || channelCache.isEmpty()) return;
        RpcHeader header = RpcHeaderFactory.getRequestHeader(RpcConstants.SERIALIZATION_PROTOSTUFF, RpcType.HEARTBEAT_FROM_CONSUMER.getType());
        RpcProtocol<RpcRequest> requestRpcProtocol = new RpcProtocol<RpcRequest>();
        RpcRequest rpcRequest = new RpcRequest();
        rpcRequest.setParameters(new Object[]{RpcConstants.HEARTBEAT_PING});
        requestRpcProtocol.setHeader(header);
        requestRpcProtocol.setBody(rpcRequest);
        // for (Channel channel : channelCache) {
        //     if (channel.isOpen() && channel.isActive()) {
        //         logger.info("send heartbeat message to service provider, the provider is: {}, the heartbeat message is: {}", channel.remoteAddress(), RpcConstants.HEARTBEAT_PING);
        //         channel.writeAndFlush(requestRpcProtocol);
        //     }
        // }
        channelCache.forEach((channel) -> {
            if (channel.isOpen() && channel.isActive()) {
                logger.info("send heartbeat message to service provider, the provider is: {}, the heartbeat message is: {}", channel.remoteAddress(), RpcConstants.HEARTBEAT_PING);
                channel.writeAndFlush(requestRpcProtocol);
            }
        });
    }
}
