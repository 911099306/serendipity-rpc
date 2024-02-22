package com.serendipity.rpc.test.consumer.codec.handler;

import com.alibaba.fastjson2.JSONObject;
import com.serendipity.rpc.protocol.RpcProtocol;
import com.serendipity.rpc.protocol.header.RpcHeaderFactory;
import com.serendipity.rpc.protocol.request.RpcRequest;
import com.serendipity.rpc.protocol.response.RpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author binghe
 * @version 1.0.0
 * @description RPC消费者处理器
 */
public class RpcTestConsumerHandler extends SimpleChannelInboundHandler<RpcProtocol<RpcResponse>> {
    private final Logger logger = LoggerFactory.getLogger(RpcTestConsumerHandler.class);

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // logger.info("RpcTestConsumerHandler发送数据开始...");
        // //模拟发送数据
        // RpcProtocol<RpcRequest> protocol = new RpcProtocol<RpcRequest>();
        // protocol.setHeader(RpcHeaderFactory.getRequestHeader("jdk"));
        // RpcRequest request = new RpcRequest();
        // request.setClassName("com.serendipity.rpc.test.api.DemoService");
        // request.setGroup("serendipity");
        // request.setMethodName("hello");
        // request.setParameters(new Object[]{"serendipity"});
        // request.setParameterTypes(new Class[]{String.class});
        // request.setVersion("1.0.0");
        // request.setAsync(false);
        // request.setOneway(false);
        // protocol.setBody(request);
        // logger.info("服务消费者发送的数据===>>>{}", JSONObject.toJSONString(protocol));
        // ctx.writeAndFlush(protocol);
        // logger.info("发送数据完毕...");

    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcProtocol<RpcResponse> protocol) throws Exception {
        // logger.info("服务消费者接收到的数据===>>>{}", JSONObject.toJSONString(protocol));
    }
}