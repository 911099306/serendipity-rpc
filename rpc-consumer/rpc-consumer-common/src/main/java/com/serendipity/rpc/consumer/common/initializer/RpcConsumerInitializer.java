package com.serendipity.rpc.consumer.common.initializer;

import com.serendipity.rpc.codec.RpcDecoder;
import com.serendipity.rpc.codec.RpcEncoder;
import com.serendipity.rpc.constants.RpcConstants;
import com.serendipity.rpc.consumer.common.handler.RpcConsumerHandler;
import com.serendipity.rpc.flow.processor.FlowPostProcessor;
import com.serendipity.rpc.threadpool.ConcurrentThreadPool;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

/**
 * @author serendipity
 * @version 1.0
 * @date 2024/2/16
 **/
public class RpcConsumerInitializer extends ChannelInitializer<SocketChannel> {

    /**
     * 心跳间隔时间
     */
    private int heartbeatInterval;
    /**
     * 线程池
     */
    private ConcurrentThreadPool concurrentThreadPool;

    /**
     * 流量分析后置处理器
     */
    private FlowPostProcessor flowPostProcessor;

    /**
     * 是否开启数据缓冲
     */
    private boolean enableBuffer;

    /**
     * 缓冲区大小
     */
    private int bufferSize;

    public RpcConsumerInitializer(int heartbeatInterval, boolean enableBuffer, int bufferSize, ConcurrentThreadPool concurrentThreadPool, FlowPostProcessor flowPostProcessor){
        if (heartbeatInterval > 0){
            this.heartbeatInterval = heartbeatInterval;
        }
        this.concurrentThreadPool = concurrentThreadPool;
        this.flowPostProcessor = flowPostProcessor;
        this.enableBuffer = enableBuffer;
        this.bufferSize = bufferSize;
    }
    @Override
    protected void initChannel(SocketChannel channel) throws Exception {
        ChannelPipeline cp = channel.pipeline();
        cp.addLast(RpcConstants.CODEC_ENCODER, new RpcEncoder(flowPostProcessor));
        cp.addLast(RpcConstants.CODEC_DECODER, new RpcDecoder(flowPostProcessor));
        cp.addLast(RpcConstants.CODEC_CLIENT_IDLE_HANDLER, new IdleStateHandler(heartbeatInterval, 0, 0, TimeUnit.MILLISECONDS));
        cp.addLast(RpcConstants.CODEC_HANDLER, new RpcConsumerHandler(enableBuffer, bufferSize, concurrentThreadPool));
    }
}
