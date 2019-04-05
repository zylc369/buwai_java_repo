package buwai.commons.thread;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * MDC线程，继承父线程的上下文
 *
 * @author 不歪
 * @version 创建时间：2019-03-26 17:51
 */
public class MDCThread {

    private MDCThreadPoolTaskExecutor executor = new MDCThreadPoolTaskExecutor(
            1, 1, 0L, TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(1),
            new ThreadPoolExecutor.AbortPolicy());

    private Runnable runnable;
    
    public MDCThread(Runnable runnable) {
        this.runnable = runnable;
    }

    public void execute() {
        executor.execute(runnable);
    }
    
    public void await() throws InterruptedException {
        if (null != executor) {
            executor.shutdown();
            while (!executor.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS)) { }
        }
    }

    public void await(long millisecond) throws InterruptedException {
        if (null != executor) {
            executor.shutdown();
            while (!executor.awaitTermination(millisecond, TimeUnit.MILLISECONDS)) { }
        }
    }

}
