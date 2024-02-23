package com.serendipity.rpc.flow.processor;

import com.serendipity.rpc.constants.RpcConstants;
import com.serendipity.rpc.protocol.header.RpcHeader;
import com.serendipity.rpc.spi.annotation.SPI;

/**
 * 流量分析后置处理器接口
 *
 * @author serendipity
 * @version 1.0
 * @date 2024/2/23
 **/
@SPI(RpcConstants.FLOW_POST_PROCESSOR_PRINT)
public interface FlowPostProcessor {

    /**
     * 打印流量
     * @param rpcHeader 消息头
     */
    void postRpcHeaderProcessor(RpcHeader rpcHeader);
}
