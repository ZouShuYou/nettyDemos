package netty.rpc.common.util;

import java.util.concurrent.*;

/**
 * @author zousy
 * @version v1.0
 * @Description
 * @date 2021-11-17 11:07
 */
public class ThreadPoolUtil {
    public static ThreadPoolExecutor makeServerThreadPool(final String serviceName,int corePoolSize,int maxPoolSize){
        ThreadPoolExecutor serverHandlerPool = new ThreadPoolExecutor(
                corePoolSize,
                maxPoolSize,
                60L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(1000),
                new ThreadFactory() {
                    @Override
                    public Thread newThread(Runnable r) {
                        return new Thread(r,"netty-rpc-" + serviceName + "-" + r.hashCode());
                    }
                },
                new ThreadPoolExecutor.AbortPolicy());
        return serverHandlerPool;
    }
}
