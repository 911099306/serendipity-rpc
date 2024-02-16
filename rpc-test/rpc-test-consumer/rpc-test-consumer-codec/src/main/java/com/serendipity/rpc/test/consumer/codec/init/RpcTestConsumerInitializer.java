package com.serendipity.rpc.test.consumer.codec.init;

import com.serendipity.rpc.codec.RpcDecoder;
import com.serendipity.rpc.codec.RpcEncoder;
import com.serendipity.rpc.test.consumer.codec.handler.RpcTestConsumerHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

/**
 * @author binghe
 * @version 1.0.0
 * @description
 */
public class RpcTestConsumerInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline cp = socketChannel.pipeline();
        cp.addLast(new RpcEncoder());
        cp.addLast(new RpcDecoder());
        cp.addLast(new RpcTestConsumerHandler());
    }
}