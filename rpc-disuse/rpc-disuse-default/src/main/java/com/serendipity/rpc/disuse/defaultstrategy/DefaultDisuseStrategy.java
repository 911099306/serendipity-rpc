package com.serendipity.rpc.disuse.defaultstrategy;

import com.serendipity.rpc.disuse.api.DisuseStrategy;
import com.serendipity.rpc.disuse.api.connection.ConnectionInfo;
import com.serendipity.rpc.spi.annotation.SPIClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 默认淘汰策略，淘汰第一个
 * @author serendipity
 * @version 1.0
 * @date 2024/2/23
 **/
@SPIClass
public class DefaultDisuseStrategy implements DisuseStrategy {
    private final Logger logger = LoggerFactory.getLogger(DefaultDisuseStrategy.class);
    @Override
    public ConnectionInfo selectConnection(List<ConnectionInfo> connectionList) {
        logger.info("execute default disuse strategy...");
        return connectionList.get(0);
    }
}
