package com.serendipity.rpc.proxy.api.consumer;

import com.serendipity.rpc.protocol.RpcProtocol;
import com.serendipity.rpc.protocol.request.RpcRequest;
import com.serendipity.rpc.proxy.api.future.RPCFuture;

/**
 * 服务消费者
 *
 * @author serendipity
 * @version 1.0
 * @date 2024/2/17
 **/
public interface Consumer {

    /**
     * 消费者发送 request 请求
     */
    RPCFuture sendRequest(RpcProtocol<RpcRequest> protocol) throws Exception;
}
