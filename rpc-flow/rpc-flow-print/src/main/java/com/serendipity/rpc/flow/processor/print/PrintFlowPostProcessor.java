package com.serendipity.rpc.flow.processor.print;

import com.serendipity.rpc.flow.processor.FlowPostProcessor;
import com.serendipity.rpc.protocol.header.RpcHeader;
import com.serendipity.rpc.spi.annotation.SPIClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author serendipity
 * @version 1.0
 * @date 2024/2/23
 **/
@SPIClass
public class PrintFlowPostProcessor implements FlowPostProcessor {

    private final Logger logger = LoggerFactory.getLogger(PrintFlowPostProcessor.class);

    @Override
    public void postRpcHeaderProcessor(RpcHeader rpcHeader) {
        logger.info(getRpcHeaderString(rpcHeader));
    }

    /**
     * 对流量信息和请求信息进行打印
     */
    private String getRpcHeaderString(RpcHeader rpcHeader){
        StringBuilder sb = new StringBuilder();
        sb.append("magic: " + rpcHeader.getMagic());
        sb.append(", requestId: " + rpcHeader.getRequestId());
        sb.append(", msgType: " + rpcHeader.getMsgType());
        sb.append(", serializationType: " + rpcHeader.getSerializationType());
        sb.append(", status: " + rpcHeader.getStatus());
        sb.append(", msgLen: " + rpcHeader.getMsgLen());

        return sb.toString();
    }
}
