package com.serendipity.rpc.buffer.cache;

import com.serendipity.rpc.constants.RpcConstants;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * 缓冲区实现
 *
 * @author serendipity
 * @version 1.0
 * @date 2024/2/24
 **/
public class BufferCacheManager<T> {
    /**
     * 缓冲队列
     */
    private BlockingQueue<T> bufferQueue;
    /**
     * 缓存管理器单例对象
     */
    private static volatile BufferCacheManager instance;

    //私有构造方法
    private BufferCacheManager(int bufferSize){
        if (bufferSize <= 0){
            bufferSize = RpcConstants.DEFAULT_BUFFER_SIZE;
        }
        this.bufferQueue = new ArrayBlockingQueue<>(bufferSize);
    }

    /**
     * 创建单例对象
     * @param bufferSize 缓冲区大小
     * @return 缓冲区
     * @param <T> 数据类型
     */
    public static <T> BufferCacheManager<T> getInstance(int bufferSize){
        if (instance == null){
            synchronized (BufferCacheManager.class){
                if (instance == null){
                    instance = new BufferCacheManager(bufferSize);
                }
            }
        }
        return instance;
    }

    /**
     * 向缓冲区添加元素
     * @param t 添加的元素
     */
    public void put(T t){
        try {
            bufferQueue.put(t);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取缓冲区元素
     */
    public T take(){
        try {
            return bufferQueue.take();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
