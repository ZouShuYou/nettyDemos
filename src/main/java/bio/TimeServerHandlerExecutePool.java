package bio;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author zousy
 * @version v1.0
 * @Description
 * @date 2020-12-14 17:54
 */
public class TimeServerHandlerExecutePool {
    private ExecutorService executorService;


    public TimeServerHandlerExecutePool(int maxPoolSize, int queueSize){
        this.executorService = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(),maxPoolSize,120L, TimeUnit.MINUTES,new ArrayBlockingQueue<Runnable>(queueSize));
    }

    public void execute(Runnable task){
        executorService.execute(task);
    }
}
