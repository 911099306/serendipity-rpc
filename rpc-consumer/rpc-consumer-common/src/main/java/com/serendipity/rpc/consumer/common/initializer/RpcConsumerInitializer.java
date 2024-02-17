package com.serendipity.rpc.consumer.common.initializer;

import com.serendipity.rpc.codec.RpcDecoder;
import com.serendipity.rpc.codec.RpcEncoder;
import com.serendipity.rpc.consumer.common.handler.RpcConsumerHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

/**
 * @author serendipity
 * @version 1.0
 * @date 2024/2/16
 **/
public class RpcConsumerInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel channel) throws Exception {
        ChannelPipeline cp = channel.pipeline();
        cp.addLast(new RpcEncoder());
        cp.addLast(new RpcDecoder());
        cp.addLast(new RpcConsumerHandler());
    }
}
