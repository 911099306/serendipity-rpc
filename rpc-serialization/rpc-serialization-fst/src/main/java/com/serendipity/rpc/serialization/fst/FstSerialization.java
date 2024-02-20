package com.serendipity.rpc.serialization.fst;

import com.serendipity.rpc.common.exception.SerializerException;
import com.serendipity.rpc.serialization.api.Serialization;
import com.serendipity.rpc.spi.annotation.SPIClass;
import org.nustaq.serialization.FSTConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Fst 序列化方式
 * @author serendipity
 * @version 1.0
 * @date 2024/2/20
 **/
@SPIClass
public class FstSerialization implements Serialization {
    private final Logger logger = LoggerFactory.getLogger(FstSerialization.class);

    @Override
    public <T> byte[] serialize(T obj) {
        logger.info("execute fst serialize...");
        if (obj == null){
            throw new SerializerException("serialize object is null");
        }
        FSTConfiguration conf = FSTConfiguration.getDefaultConfiguration();
        return conf.asByteArray(obj);
    }

    @Override
    public <T> T deserialize(byte[] data, Class<T> cls) {
        logger.info("execute fst deserialize...");
        if (data == null){
            throw new SerializerException("deserialize data is null");
        }
        FSTConfiguration conf = FSTConfiguration.getDefaultConfiguration();
        return (T) conf.asObject(data);
    }
}