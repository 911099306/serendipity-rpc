package com.serendipity.rpc.test.scanner.provider;

import com.serendipity.rpc.annotation.RpcService;
import com.serendipity.rpc.test.scanner.service.DemoService;

/**
 * @author serendipity
 * @version 1.0
 * @date 2024/2/4
 **/
@RpcService(interfaceClass = DemoService.class, interfaceClassName = "com.serendipity.rpc.test.scanner.service.DemoService", version = "1.0.0", group = "serendipity")
public class ProviderDemoServiceImpl implements DemoService {
}
