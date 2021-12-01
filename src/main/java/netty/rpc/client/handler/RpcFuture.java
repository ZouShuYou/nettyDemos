package netty.rpc.client.handler;

import netty.rpc.client.RpcClient;
import netty.rpc.common.codec.RpcRequest;
import netty.rpc.common.codec.RpcResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author zousy
 * @version v1.0
 * @Description
 * @date 2021-12-01 16:12
 */
public class RpcFuture implements Future<Object> {
    private static final Logger logger = LoggerFactory.getLogger(RpcFuture.class);

    private Sync sync;
    private RpcRequest request;
    private RpcResponse response;
    private long startTime;
    private long responseTimeThreshold = 5000;
    private List<AsyncRPCCallback> pendingCallbacks = new ArrayList<>();
    private ReentrantLock lock = new ReentrantLock();

    public RpcFuture(RpcRequest request) {
        this.sync = new Sync();
        this.request = request;
        this.startTime = System.currentTimeMillis();
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isCancelled() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isDone() {
        return sync.isDone();
    }

    @Override
    public Object get(){
        sync.acquire(1);
        if (this.response != null){
            return this.response.getResult();
        }else {
            return null;
        }
    }

    @Override
    public Object get(long timeout, TimeUnit unit) throws InterruptedException{
        boolean success = sync.tryAcquireNanos(1, unit.toNanos(timeout));
        if (success){
            if (this.response != null){
                return this.response.getResult();
            }else {
                return null;
            }
        }else {
            throw new RuntimeException("Timeout exception. Request id: " + this.request.getRequestId()
                    + ". Request class name: " + this.request.getClassName()
                    + ". Request method: " + this.request.getMethodName());
        }
    }

    public void done(RpcResponse response){
        this.response = response;
        sync.release(1);

    }

    private void invokeCallbacks(){
        lock.lock();
        try {
            for (AsyncRPCCallback callback : pendingCallbacks){
                runCallback(callback);
            }
        } finally {
            lock.unlock();
        }
    }

    private void runCallback(AsyncRPCCallback callback) {
        final RpcResponse res = this.response;
        RpcClient.submit(new Runnable(){

            @Override
            public void run() {
                if (!res.isError()){
                    callback.success(res.getResult());
                }else {
                    callback.fail(new RuntimeException("Response Error", new Throwable(res.getError())));
                }
            }
        });
    }

    public RpcFuture addCallback(AsyncRPCCallback callback){
        lock.lock();
        try {
            if (isDone()){
                runCallback(callback);
            }else {
                this.pendingCallbacks.add(callback);
            }
        }finally {
            lock.unlock();
        }
        return this;
    }

    static class Sync extends AbstractQueuedSynchronizer{
        private final int done = 1;
        private final int pending = 0;

        @Override
        protected boolean tryAcquire(int arg) {
            return getState() == done;
        }

        @Override
        protected boolean tryRelease(int arg) {
            if (getState() == pending){
                if (compareAndSetState(pending,done)){
                    return true;
                }else {
                    return false;
                }
            }else {
                return true;
            }
        }

        protected boolean isDone() {
            return getState() == done;
        }

    }
}
