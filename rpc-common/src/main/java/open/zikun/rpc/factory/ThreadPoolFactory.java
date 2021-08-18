package open.zikun.rpc.factory;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.*;


/**
 * 线程池工厂
 */
public class ThreadPoolFactory {

    private static final int CORE_POOL_SIZE = 10;
    private static final int MAXIMUM_POOL_SIZE = 100;
    private static final int KEEP_ALIVE_TIME = 1;
    private static final int BLOCKING_QUEUE_CAPACITY = 100;

    private static final Logger logger = LoggerFactory.getLogger(ThreadPoolFactory.class);

    private static Map<String, ExecutorService> threadPoolMap = new ConcurrentHashMap<>();

    public ThreadPoolFactory() {
    }

    public static ExecutorService createDefaultTheadPool(String threadNamePrefix) {
        return createTheadPool(threadNamePrefix, false);
    }


    public static ExecutorService createDefaultThreadPool(String threadNamePrefix, boolean daemon) {
        ExecutorService pool = threadPoolMap.computeIfAbsent(threadNamePrefix, k -> createTheadPool(threadNamePrefix, daemon));
        if (pool.isShutdown() | pool.isTerminated()) {
            threadPoolMap.remove(threadNamePrefix);
            pool = createTheadPool(threadNamePrefix, daemon);
            threadPoolMap.put(threadNamePrefix, pool);
        }
        return pool;
    }

    public static ExecutorService createTheadPool(String threadNamePrefix, boolean daemon) {
        // 使用有界队列
        BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(BLOCKING_QUEUE_CAPACITY);
        ThreadFactory threadFactory = createTheadFactory(threadNamePrefix, daemon);
        return new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_TIME, TimeUnit.MINUTES, workQueue, threadFactory);

    }

    private static ThreadFactory createTheadFactory(String threadNamePrefix, Boolean daemon) {
        if (threadNamePrefix != null) {
            if (daemon != null) {
                return new ThreadFactoryBuilder().setNameFormat(threadNamePrefix + "-%d").setDaemon(daemon).build();
            } else {
                return new ThreadFactoryBuilder().setNameFormat(threadNamePrefix + "-%d").build();
            }
        }
        return Executors.defaultThreadFactory();
    }

    public static void shutDownAll() {
        logger.info("关闭所有的线程池！");
        threadPoolMap.entrySet().parallelStream().forEach(entry -> {
            ExecutorService executorService = entry.getValue();
            executorService.shutdown();
            logger.info("关闭线程池[{}][{}]", entry.getKey(), executorService.isShutdown());
            try {
                executorService.awaitTermination(10, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                logger.error("关闭线程池失败！", e);
                executorService.shutdown();
            }
        });

    }
}
