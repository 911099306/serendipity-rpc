package com.serendipity.rpc.consumer.common.callback;

/**
 * 异步回调接口
 *
 * @author serendipity
 * @version 1.0
 * @date 2024/2/17
 **/
public interface AsyncRPCCallback {

    /**
     * 成功后的回调方法
     */
    void onSuccess(Object result);

    /**
     * 异常的回调方法
     */
    void onException(Exception e);
}
