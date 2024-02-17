package com.serendipity.rpc.proxy.api.async;

import com.serendipity.rpc.proxy.api.future.RPCFuture;

/**
 * 异步调用接口
 *
 * @author serendipity
 * @version 1.0
 * @date 2024/2/18
 **/
public interface IAsyncObjectProxy {

    /**
     * 异步代理对象调用方法
     * @param funcName 方法名称
     * @param args 方法参数
     * @return 封装好的 RPCFuture 对象
     */
    RPCFuture call(String funcName, Object... args);
}
