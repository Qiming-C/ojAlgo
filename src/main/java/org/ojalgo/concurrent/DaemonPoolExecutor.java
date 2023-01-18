/*
 * Copyright 1997-2022 Optimatika
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.ojalgo.concurrent;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import org.ojalgo.OjAlgoUtils;

public final class DaemonPoolExecutor extends ThreadPoolExecutor {

    private static final AtomicInteger COUNTER = new AtomicInteger();
    private static final ThreadGroup GROUP = new ThreadGroup("ojAlgo-daemon-group");

    static final DaemonPoolExecutor INSTANCE = new DaemonPoolExecutor(OjAlgoUtils.ENVIRONMENT.units, Integer.MAX_VALUE, 5L, TimeUnit.SECONDS,
            new SynchronousQueue<Runnable>(), DaemonPoolExecutor.newThreadFactory("ojAlgo-daemon-"));

    /**
     *See {@link java.util.concurrent.AbstractExecutorService#submit(java.util.concurrent.Callable)}.
 
     */
    public static <T> Future<T> invoke( Callable<T> task) {
        return INSTANCE.submit(task);
    }

    /**
     *See {@link java.util.concurrent.AbstractExecutorService#submit(java.lang.Runnable)}.
 
     */
    public static Future<?> invoke( Runnable task) {
        return INSTANCE.submit(task);
    }

    /**
     *See {@link java.util.concurrent.AbstractExecutorService#submit(java.lang.Runnable, java.lang.Object)}.
 
     */
    public static <T> Future<T> invoke( Runnable task,  T result) {
        return INSTANCE.submit(task, result);
    }

    /**
     * Like {@link Executors#newCachedThreadPool()} but with identifiable (daemon) threads
     */
    public static ExecutorService newCachedThreadPool( String name) {
        return Executors.newCachedThreadPool(DaemonPoolExecutor.newThreadFactory(name));
    }

    /**
     * Like {@link Executors#newFixedThreadPool(int)} but with identifiable (daemon) threads
     */
    public static ExecutorService newFixedThreadPool( String name,  int nThreads) {
        return Executors.newFixedThreadPool(nThreads, DaemonPoolExecutor.newThreadFactory(name));
    }

    /**
     * Like {@link Executors#newScheduledThreadPool(int)} but with identifiable (daemon) threads
     */
    public static ExecutorService newScheduledThreadPool( String name,  int corePoolSize) {
        return Executors.newScheduledThreadPool(corePoolSize, DaemonPoolExecutor.newThreadFactory(name));
    }

    /**
     * Like {@link Executors#newSingleThreadExecutor()} but with identifiable (daemon) threads
     */
    public static ExecutorService newSingleThreadExecutor( String name) {
        return Executors.newSingleThreadExecutor(DaemonPoolExecutor.newThreadFactory(name));
    }

    /**
     * Like {@link Executors#newSingleThreadScheduledExecutor()} but with identifiable (daemon) threads
     */
    public static ExecutorService newSingleThreadScheduledExecutor( String name) {
        return Executors.newSingleThreadScheduledExecutor(DaemonPoolExecutor.newThreadFactory(name));
    }

    public static ThreadFactory newThreadFactory( String name) {
        return DaemonPoolExecutor.newThreadFactory(GROUP, name);
    }

    public static ThreadFactory newThreadFactory( ThreadGroup group,  String name) {

        String prefix = name.endsWith("-") ? name : name + "-";

        return target -> {
            var thread = new Thread(group, target, prefix + DaemonPoolExecutor.COUNTER.incrementAndGet());
            thread.setDaemon(true);
            return thread;
        };
    }

    DaemonPoolExecutor( int corePoolSize,  int maximumPoolSize,  long keepAliveTime,  TimeUnit unit,
             BlockingQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
    }

    DaemonPoolExecutor( int corePoolSize,  int maximumPoolSize,  long keepAliveTime,  TimeUnit unit,
             BlockingQueue<Runnable> workQueue,  RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, handler);
    }

    DaemonPoolExecutor( int corePoolSize,  int maximumPoolSize,  long keepAliveTime,  TimeUnit unit,
             BlockingQueue<Runnable> workQueue,  ThreadFactory threadFactory) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
    }

    DaemonPoolExecutor( int corePoolSize,  int maximumPoolSize,  long keepAliveTime,  TimeUnit unit,
             BlockingQueue<Runnable> workQueue,  ThreadFactory threadFactory,  RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
    }

}
