package com.serendipity.rpc.consumer.common.helper;

import com.serendipity.rpc.consumer.common.handler.RpcConsumerHandler;
import com.serendipity.rpc.protocol.meta.ServiceMeta;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 缓存服务消费者处理器 RpcConsumerHandler 类的实例
 *
 * @author serendipity
 * @version 1.0
 * @date 2024/2/19
 **/
public class RpcConsumerHandlerHelper {

    private static Map<String, RpcConsumerHandler> rpcConsumerHandlerMap;

    static {
        // 为什么使用 ConcurrentHashMap
        rpcConsumerHandlerMap = new ConcurrentHashMap<>();
    }

    private static String getKey(ServiceMeta key) {
        return key.getServiceAddr().concat("_").concat(String.valueOf(key.getServicePort()));
    }

    public static void put(ServiceMeta key, RpcConsumerHandler value) {
        rpcConsumerHandlerMap.put(getKey(key), value);
    }

    public static RpcConsumerHandler get(ServiceMeta key) {
        return rpcConsumerHandlerMap.get(getKey(key));
    }

    public static void closeRpcClientHandler() {
        Collection<RpcConsumerHandler> rpcConsumerHandlers = rpcConsumerHandlerMap.values();
        if (rpcConsumerHandlers != null) {
            rpcConsumerHandlers.stream().forEach(rpcConsumerHandler -> {
                rpcConsumerHandler.close();
            });
        }
        rpcConsumerHandlerMap.clear();
    }
}


