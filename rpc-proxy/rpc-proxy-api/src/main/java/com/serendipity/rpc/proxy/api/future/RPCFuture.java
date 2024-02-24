package com.serendipity.rpc.proxy.api.future;


import com.serendipity.rpc.common.threadpoll.ClientThreadPool;
import com.serendipity.rpc.protocol.RpcProtocol;
import com.serendipity.rpc.protocol.enumeration.RpcStatus;
import com.serendipity.rpc.protocol.header.RpcHeader;
import com.serendipity.rpc.protocol.request.RpcRequest;
import com.serendipity.rpc.protocol.response.RpcResponse;
import com.serendipity.rpc.proxy.api.callback.AsyncRPCCallback;
import com.serendipity.rpc.threadpool.ConcurrentThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.ReentrantLock;

/**
 * RPC 框架获取异步结果的 自定义Future
 *
 * @author serendipity
 * @version 1.0
 * @date 2024/2/17
 **/
public class RPCFuture extends CompletableFuture<Object> {
    private static final Logger logger = LoggerFactory.getLogger(RPCFuture.class);

    /**
     * 内部类 Sync 的实例对象
     */
    private Sync sync;

    /**
     * RpcRequest类型的协议对象
     */
    private RpcProtocol<RpcRequest> requestRpcProtocol;

    /**
     * RpcResponse类型的协议对象
     */
    private RpcProtocol<RpcResponse> responseRpcProtocol;

    /**
     * 开始时间
     */
    private long startTime;

    /**
     * 默认的超时时间
     */
    private long responseTimeThreshold = 5000;

    /**
     * 存放回调接口
     */
    private List<AsyncRPCCallback> pendingCallbacks = new ArrayList<AsyncRPCCallback>();

    /**
     * 添加、执行回调方法时 进行上锁
     */
    private ReentrantLock lock = new ReentrantLock();

    /**
     * 线程池
     */
    private ConcurrentThreadPool concurrentThreadPool;

    public RPCFuture(RpcProtocol<RpcRequest> requestRpcProtocol, ConcurrentThreadPool concurrentThreadPool) {
        this.sync = new Sync();
        this.requestRpcProtocol = requestRpcProtocol;
        this.startTime = System.currentTimeMillis();
        this.concurrentThreadPool = concurrentThreadPool;
    }

    @Override
    public boolean isDone() {
        return sync.isDone();
    }

    @Override
    public Object get() throws InterruptedException, ExecutionException {
        sync.acquire(-1);
//        if (this.responseRpcProtocol != null) {
//            return this.responseRpcProtocol.getBody().getResult();
//        } else {
//            return null;
//        }
        return this.getResult(this.responseRpcProtocol);
    }

    @Override
    public Object get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        boolean success = sync.tryAcquireNanos(-1, unit.toNanos(timeout));
        if (success) {
//            if (this.responseRpcProtocol != null) {
//                return this.responseRpcProtocol.getBody().getResult();
//            } else {
//                return null;
//            }
            return this.getResult(this.responseRpcProtocol);
        } else {
            throw new RuntimeException("Timeout exception. Request id: " + this.requestRpcProtocol.getHeader().getRequestId()
                    + ". Request class name: " + this.requestRpcProtocol.getBody().getClassName()
                    + ". Request method: " + this.requestRpcProtocol.getBody().getMethodName());
        }
    }

    /**
     * 获取最终结果
     */
    private Object getResult(RpcProtocol<RpcResponse> responseRpcProtocol){
        if (responseRpcProtocol == null){
            return null;
        }
        RpcHeader header = responseRpcProtocol.getHeader();
        //服务提供者抛出了异常
        if ((byte) RpcStatus.FAIL.getCode() == header.getStatus()){
            throw new RuntimeException("rpc provider throws exception...");
        }
        return responseRpcProtocol.getBody().getResult();
    }

    @Override
    public boolean isCancelled() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        throw new UnsupportedOperationException();
    }

    public void done(RpcProtocol<RpcResponse> responseRpcProtocol) {
        this.responseRpcProtocol = responseRpcProtocol;
        sync.release(1);
        invokeCallbacks();
        // Threshold
        long responseTime = System.currentTimeMillis() - startTime;
        if (responseTime > this.responseTimeThreshold) {
            logger.warn("Service response time is too slow. Request id = " + responseRpcProtocol.getHeader().getRequestId() + ". Response Time = " + responseTime + "ms");
        }
    }

    private void invokeCallbacks() {
        lock.lock();
        try {
            for (final AsyncRPCCallback callback : pendingCallbacks) {
                runCallback(callback);
            }
        } finally {
            lock.unlock();
        }
    }

    public RPCFuture addCallback(AsyncRPCCallback callback) {
        lock.lock();
        try {
            if (isDone()) {
                runCallback(callback);
            } else {
                this.pendingCallbacks.add(callback);
            }
        } finally {
            lock.unlock();
        }
        return this;
    }

    private void runCallback(final AsyncRPCCallback callback) {
        final RpcResponse res = this.responseRpcProtocol.getBody();
        concurrentThreadPool.submit(() -> {
            if (!res.isError()) {
                callback.onSuccess(res.getResult());
            } else {
                callback.onException(new RuntimeException("Response error", new Throwable(res.getError())));
            }
        });
    }

    static class Sync extends AbstractQueuedSynchronizer {

        private static final long serialVersionUID = 1L;

        //future status
        private final int done = 1;
        private final int pending = 0;

        @Override
        protected boolean tryAcquire(int acquires) {
            return getState() == done;
        }

        @Override
        protected boolean tryRelease(int releases) {
            if (getState() == pending) {
                if (compareAndSetState(pending, done)) {
                    return true;
                }
            }
            return false;
        }

        public boolean isDone() {
            getState();
            return getState() == done;
        }
    }
}
