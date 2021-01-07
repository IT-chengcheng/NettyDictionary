package com.BIOStudy;

import java.util.concurrent.*;

public class TimeServerHandlerExecutorPool implements Executor{

     private ExecutorService executorService;

    public TimeServerHandlerExecutorPool(int maxPoolSize, int queueSize) {

        /**
         * @param corePoolSize 核心线程数量
         * @param maximumPoolSize 线程创建最大数量
         * @param keepAliveTime 当创建到了线程池最大数量时  多长时间线程没有处理任务，则线程销毁
         * @param unit keepAliveTime时间单位
         * @param workQueue 此线程池使用什么队列
         */
        this.executorService = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(),
                maxPoolSize,120L, TimeUnit.SECONDS,new ArrayBlockingQueue<Runnable>(queueSize));
    }


    public TimeServerHandlerExecutorPool(int corePoolSize,int maxPoolSize, int queueSize) {

        /**
         * @param corePoolSize 核心线程数量
         * @param maximumPoolSize 线程创建最大数量
         * @param keepAliveTime 当创建到了线程池最大数量时  多长时间线程没有处理任务，则线程销毁
         * @param unit keepAliveTime时间单位
         * @param workQueue 此线程池使用什么队列
         */
        this.executorService = new ThreadPoolExecutor(corePoolSize,
                maxPoolSize,120L, TimeUnit.SECONDS,new LinkedBlockingDeque(queueSize));
    }

    @Override
    public void execute(Runnable command) {
        executorService.execute(command);
    }


    public static void main(String[] args) {
        TimeServerHandlerExecutorPool timeServerHandlerExecutorPool=new TimeServerHandlerExecutorPool(0,20);
        while (true){
            timeServerHandlerExecutorPool.execute(new Runnable() {
                @Override
                public void run() {
                    System.out.println("aaaaaa");
                }
            });
        }
    }


}
