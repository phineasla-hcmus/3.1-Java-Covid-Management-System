package com.seasidechachacha.client.global;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * A thin static wrapper for {@code ExecutorService}
 * 
 * @see <a href="https://stackoverflow.com/a/30250308/12405558">
 *      Using threads to make database requests
 *      </a>
 * @see <a href="https://stackoverflow.com/q/3332832/12405558">
 *      Graceful shutdown of threads and executor
 *      </a>
 * @see <a href="https://stackoverflow.com/q/13784333/12405558">
 *      Platform.runLater and Task in JavaFX
 *      </a>
 */
public class TaskExecutor {
    private static ExecutorService executorService;

    /**
     * Create an ExecutorService with {@link Executors#newCachedThreadPool()}
     */
    public static void initialize() {
        executorService = Executors.newCachedThreadPool();
    }

    public static void initialize(ExecutorService service) {
        executorService = service;
    }

    /**
     * Executes the given command at some time in the future. The command
     * may execute in a new thread, in a pooled thread, or in the calling
     * thread, at the discretion of the {@code Executor} implementation.
     *
     * @param command the runnable task
     * @throws RejectedExecutionException if this task cannot be
     *                                    accepted for execution
     * @throws NullPointerException       if command is null
     */
    public static void execute(Runnable command) {
        executorService.execute(command);
    }

    /**
     * Initiates an orderly shutdown in which previously submitted
     * tasks are executed, but no new tasks will be accepted.
     * Invocation has no additional effect if already shut down.
     *
     * <p>
     * This method does not wait for previously submitted tasks to
     * complete execution. Use {@link #awaitTermination awaitTermination}
     * to do that.
     *
     * @throws SecurityException if a security manager exists and
     *                           shutting down this ExecutorService may manipulate
     *                           threads that the caller is not permitted to modify
     *                           because it does not hold {@link
     *                           java.lang.RuntimePermission}{@code ("modifyThread")},
     *                           or the security manager's {@code checkAccess}
     *                           method
     *                           denies access.
     */
    public static void shutdown() {
        executorService.shutdown();
    }

    /**
     * Attempts to stop all actively executing tasks, halts the
     * processing of waiting tasks, and returns a list of the tasks
     * that were awaiting execution.
     *
     * <p>
     * This method does not wait for actively executing tasks to
     * terminate. Use {@link #awaitTermination awaitTermination} to
     * do that.
     *
     * <p>
     * There are no guarantees beyond best-effort attempts to stop
     * processing actively executing tasks. For example, typical
     * implementations will cancel via {@link Thread#interrupt}, so any
     * task that fails to respond to interrupts may never terminate.
     *
     * @return list of tasks that never commenced execution
     * @throws SecurityException if a security manager exists and
     *                           shutting down this ExecutorService may manipulate
     *                           threads that the caller is not permitted to modify
     *                           because it does not hold {@link
     *                           java.lang.RuntimePermission}{@code ("modifyThread")},
     *                           or the security manager's {@code checkAccess}
     *                           method
     *                           denies access.
     */
    public static List<Runnable> shutdownNow() {
        return executorService.shutdownNow();
    }

    /**
     * Blocks until all tasks have completed execution after a shutdown
     * request, or the timeout occurs, or the current thread is
     * interrupted, whichever happens first.
     *
     * @param timeout the maximum time to wait
     * @param unit    the time unit of the timeout argument
     * @return {@code true} if this executor terminated and
     *         {@code false} if the timeout elapsed before termination
     * @throws InterruptedException if interrupted while waiting
     */
    public static boolean awaitTermination(long timeout, TimeUnit unit)
            throws InterruptedException {
        return executorService.awaitTermination(timeout, unit);
    }

    /**
     * Expose the internal ExecutorService, you should only use this if static
     * methods are not enough
     * 
     * @return ExecutorService
     */
    public static ExecutorService getExecutorService() {
        return executorService;
    }
}
