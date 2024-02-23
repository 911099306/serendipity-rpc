package com.serendipity.rpc.disuse.api;

import com.serendipity.rpc.constants.RpcConstants;
import com.serendipity.rpc.disuse.api.connection.ConnectionInfo;
import com.serendipity.rpc.spi.annotation.SPI;

import java.util.List;

/**
 * @author serendipity
 * @version 1.0
 * @date 2024/2/23
 **/
@SPI(RpcConstants.RPC_CONNECTION_DISUSE_STRATEGY_DEFAULT)
public interface DisuseStrategy {

    /**
     * 从连接列表中根据规则获取一个连接对象
     * @param connectionList 所有链接集合
     * @return 准备淘汰的连接
     */
    ConnectionInfo selectConnection(List<ConnectionInfo> connectionList);
}
