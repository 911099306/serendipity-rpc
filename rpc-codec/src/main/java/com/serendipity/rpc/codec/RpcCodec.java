package com.serendipity.rpc.codec;

import com.serendipity.rpc.serialization.api.Serialization;
import com.serendipity.rpc.serialization.jdk.JdkSerialization;

import java.io.Serializable;

/**
 * @author serendipity
 * @version 1.0
 * @date 2024/2/5
 **/
public interface RpcCodec {
    default Serialization getJdkSerialization() {
        return new JdkSerialization();
    }
}
