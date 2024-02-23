package com.serendipity.rpc.codec;

import com.serendipity.rpc.flow.processor.FlowPostProcessor;
import com.serendipity.rpc.protocol.header.RpcHeader;
import com.serendipity.rpc.serialization.api.Serialization;
import com.serendipity.rpc.serialization.jdk.JdkSerialization;
import com.serendipity.rpc.spi.loader.ExtensionLoader;
import com.serendipity.rpc.threadpool.FlowPostProcessorThreadPool;

import java.io.Serializable;

/**
 * 实现编解码的接口，提供序列化和反序列化的默认方法
 *
 * @author serendipity
 * @version 1.0
 * @date 2024/2/5
 **/
public interface RpcCodec {

    /**
     * 根据serializationType通过SPI获取序列化句柄
     *
     * @param serializationType 序列话方式
     * @return Serialization 对象
     */
    default Serialization getSerialization(String serializationType) {
        return ExtensionLoader.getExtension(Serialization.class, serializationType);
    }

    /**
     * 调用RPC框架流量分析后置处理器
     *
     * @param postProcessor 后置处理器
     * @param header 封装了流量信息的消息头
     */
    default void postFlowProcessor(FlowPostProcessor postProcessor, RpcHeader header){
        //异步调用流控分析后置处理器
        FlowPostProcessorThreadPool.submit(() -> {
            postProcessor.postRpcHeaderProcessor(header);
        });
    }
}
