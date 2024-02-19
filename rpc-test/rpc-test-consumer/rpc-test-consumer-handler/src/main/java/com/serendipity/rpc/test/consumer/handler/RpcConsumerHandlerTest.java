package com.serendipity.rpc.test.consumer.handler;

import com.serendipity.rpc.consumer.common.RpcConsumer;
import com.serendipity.rpc.consumer.common.context.RpcContext;
import com.serendipity.rpc.protocol.RpcProtocol;
import com.serendipity.rpc.protocol.header.RpcHeaderFactory;
import com.serendipity.rpc.protocol.request.RpcRequest;
import com.serendipity.rpc.proxy.api.callback.AsyncRPCCallback;
import com.serendipity.rpc.proxy.api.future.RPCFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author serendipity
 * @version 1.0
 * @date 2024/2/17
 **/
public class RpcConsumerHandlerTest {

    private static final Logger logger = LoggerFactory.getLogger(RpcConsumerHandlerTest.class);
    // public static void main(String[] args) throws Exception {
    //     // mainOneWay(args);
    //
    //     RpcConsumer consumer = RpcConsumer.getInstance();
    //     RPCFuture rpcFuture = consumer.sendRequest(getRpcRequestProtocolSync());
    //     rpcFuture.addCallback(new AsyncRPCCallback() {
    //         @Override
    //         public void onSuccess(Object result) {
    //             logger.info("这是回调获得的信息 ==> {}", result);
    //         }
    //         @Override
    //         public void onException(Exception e) {
    //             logger.info("抛出了异常 ==> {}", e.getMessage());
    //         }
    //     });
    //     Thread.sleep(200);
    //     consumer.close();
    //
    // }
    //
    // public static void mainOneWay(String[] args) throws Exception {
    //     RpcConsumer consumer = RpcConsumer.getInstance();
    //     consumer.sendRequest(getRpcRequestProtocolOneWay());
    //     logger.info("无需获取返回的结果数据");
    //     consumer.close();
    // }
    //
    // public static void mainAsync(String[] args) throws Exception {
    //     RpcConsumer consumer = RpcConsumer.getInstance();
    //     consumer.sendRequest(getRpcRequestProtocolAsync());
    //     RPCFuture future = RpcContext.getContext().getRPCFuture();
    //     logger.info("从服务消费者获取到的数据===>>>" + future.get());
    //     consumer.close();
    // }
    //
    // public static void mainSync(String[] args) throws Exception {
    //     RpcConsumer consumer = RpcConsumer.getInstance();
    //     RPCFuture future = consumer.sendRequest(getRpcRequestProtocolSync());
    //     logger.info("从服务消费者获取到的数据===>>>" + future.get());
    //     consumer.close();
    // }

    private static RpcProtocol<RpcRequest> getRpcRequestProtocolOneWay(){
        //模拟发送数据
        RpcProtocol<RpcRequest> protocol = new RpcProtocol<RpcRequest>();
        protocol.setHeader(RpcHeaderFactory.getRequestHeader("jdk"));
        RpcRequest request = new RpcRequest();
        request.setClassName("com.serendipity.rpc.test.api.DemoService");
        request.setGroup("serendipity");
        request.setMethodName("hello");
        request.setParameters(new Object[]{"serendipity"});
        request.setParameterTypes(new Class[]{String.class});
        request.setVersion("1.0.0");
        request.setAsync(false);
        request.setOneway(true);
        protocol.setBody(request);
        return protocol;
    }

    private static RpcProtocol<RpcRequest> getRpcRequestProtocolAsync(){
        //模拟发送数据
        RpcProtocol<RpcRequest> protocol = new RpcProtocol<RpcRequest>();
        protocol.setHeader(RpcHeaderFactory.getRequestHeader("jdk"));
        RpcRequest request = new RpcRequest();
        request.setClassName("com.serendipity.rpc.test.api.DemoService");
        request.setGroup("serendipity");
        request.setMethodName("hello");
        request.setParameters(new Object[]{"serendipity"});
        request.setParameterTypes(new Class[]{String.class});
        request.setVersion("1.0.0");
        request.setAsync(true);
        request.setOneway(false);
        protocol.setBody(request);
        return protocol;
    }

    private static RpcProtocol<RpcRequest> getRpcRequestProtocolSync(){
        //模拟发送数据
        RpcProtocol<RpcRequest> protocol = new RpcProtocol<RpcRequest>();
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
