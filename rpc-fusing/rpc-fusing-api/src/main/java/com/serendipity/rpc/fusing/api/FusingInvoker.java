package com.serendipity.rpc.fusing.api;

import com.serendipity.rpc.constants.RpcConstants;
import com.serendipity.rpc.spi.annotation.SPI;

/**
 * @author serendipity
 * @version 1.0
 * @date 2024/2/25
 **/
@SPI(RpcConstants.DEFAULT_FUSING_INVOKER)
public interface FusingInvoker {

    /**
     * 是否会触发熔断操作，规则如下：
     * 1.断路器默认处于“关闭”状态，当错误个数或错误率到达阈值，就会触发断路器“开启”。
     * 2.断路器开启后进入熔断时间，到达熔断时间终点后重置熔断时间，进入“半开启”状态。
     * 3.在半开启状态下，如果服务能力恢复，则断路器关闭熔断状态。进而进入正常的服务状态。
     * 4.在半开启状态下，如果服务能力未能恢复，则断路器再次触发服务熔断，进入熔断时间。
     * @return 是否要触发熔断，true：触发熔断，false：不触发熔断
     */
    boolean invokeFusingStrategy();

    /**
     * 处理请求的次数
     */
    void incrementCount();

    /**
     * 处理请求失败的次数
     */
    void incrementFailureCount();

    /**
     * 在milliSeconds毫秒内错误数量或者错误百分比达到totalFailure，则触发熔断操作
     * @param totalFailure 在milliSeconds毫秒内触发熔断操作的上限值
     * @param milliSeconds 毫秒数
     */
    default void init(double totalFailure, int milliSeconds){}
}