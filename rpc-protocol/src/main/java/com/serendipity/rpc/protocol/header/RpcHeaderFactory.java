package com.serendipity.rpc.protocol.header;

import com.serendipity.rpc.common.id.IdFactory;
import com.serendipity.rpc.constants.RpcConstants;
import com.serendipity.rpc.protocol.enumeration.RpcType;

/**
 * @author serendipity
 * @version 1.0
 * @date 2024/2/5
 **/
public class RpcHeaderFactory {

    public static RpcHeader getRequestHeader(String serializationType, int messageType) {
        RpcHeader header = new RpcHeader();
        long requestId = IdFactory.getId();
        header.setMagic(RpcConstants.MAGIC);
        header.setRequestId(requestId);
        header.setMsgType((byte) messageType);
        header.setStatus((byte) 0x1);
        header.setSerializationType(serializationType);
        return header;
    }
}
