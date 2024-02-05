package com.serendipity.rpc.common.exception;

/**
 * @author serendipity
 * @version 1.0
 * @date 2024/2/5
 **/
public class SerializerException extends RuntimeException{

    private static final long serialVersionUID = -6783134254669118520L;

    /**
     * 抛出一个序列化异常
     * @param e 异常
     */
    public SerializerException(final Throwable e) {
        super(e);
    }

    /**
     * 抛出一个序列化异常
     * @param message 异常原因
     */
    public SerializerException(final String message) {
        super(message);
    }

    /**
     * 抛出一个序列化异常
     * @param message 异常原因
     * @param e 异常
     */
    public SerializerException(final String message,final Throwable e){
        super(message, e);
    }
}
