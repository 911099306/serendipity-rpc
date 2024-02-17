package com.serendipity.rpc.test.consumer.handler;

import com.serendipity.rpc.consumer.common.RpcConsumer;
import com.serendipity.rpc.consumer.common.future.RPCFuture;
import com.serendipity.rpc.protocol.RpcProtocol;
import com.serendipity.rpc.protocol.header.RpcHeaderFactory;
import com.serendipity.rpc.protocol.request.RpcRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Future;

/**
 * @author serendipity
 * @version 1.0
 * @date 2024/2/17
 **/
public class RpcConsumerHandlerTest {

    private static final Logger logger = LoggerFactory.getLogger(RpcConsumerHandlerTest.class);
    public static void main(String[] args) throws Exception {
        RpcConsumer consumer = RpcConsumer.getInstance();
        RPCFuture future = consumer.sendRequest(getRpcRequestProtocol());
        logger.info("服务消费者最终获取到的数据===>>>" + future.get());
        consumer.close();
    }

    private static RpcProtocol<RpcRequest> getRpcRequestProtocol(){
        // 模拟发送数据
        RpcProtocol<RpcRequest> protocol = new RpcProtocol<>();
        protocol.setHeader(RpcHeaderFactory.getRequestHeader("jdk"));
        RpcRequest request = new RpcRequest();
        request.setClassName("com.serendipity.rpc.test.api.DemoService");
        request.setGroup("serendipity");
        request.setMethodName("hello");
        request.setParameters(new Object[]{"serendipity"});
        request.setParameterTypes(new Class[]{String.class});
        request.setVersion("1.0.0");
        request.setAsync(false);
        request.setOneway(false);
        protocol.setBody(request);
        return protocol;
    }

}
