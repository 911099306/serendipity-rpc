package com.serendipity.rpc.test.scanner.consumer.service.impl;

import com.serendipity.rpc.annotation.RpcReference;
import com.serendipity.rpc.annotation.RpcService;
import com.serendipity.rpc.test.scanner.consumer.service.ConsumerBusinessService;
import com.serendipity.rpc.test.scanner.service.DemoService;

/**
 * @author serendipity
 * @version 1.0
 * @date 2024/2/4
 **/
public class ConsumerBusinessServiceImpl implements ConsumerBusinessService {

    @RpcReference(registryType = "zookeeper", registryAddress = "127.0.0.1:2181", version = "1.0.0", group = "serendipity")
    private DemoService demoService;

}
