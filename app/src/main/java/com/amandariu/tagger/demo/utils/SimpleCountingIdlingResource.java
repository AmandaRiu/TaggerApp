package com.amandariu.tagger.demo.utils;

import android.support.test.espresso.IdlingResource;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * An simple counter implementation of {@link IdlingResource} that determines idleness by
 * maintaining an internal counter. When the counter is 0 - it is considered to be idle, when it is
 * non-zero it is not idle. This is very similar to the way a {@link java.util.concurrent.Semaphore}
 * behaves.
 * <p>
 * This class can then be used to wrap up operations that while in progress should block tests from
 * accessing the UI.
 */
class SimpleCountingIdlingResource implements IdlingResource {

    private final String mResourceName;
    private final AtomicInteger mCounter = new AtomicInteger(0);
    //
    // written from main thread, read from any thread.
    private volatile ResourceCallback mResourceCallback;

    /**
     * Creates a SimpleCountingIdlingResource
     *
     * @param resourceName the resource name this resource should report to Espresso.
     */
    public SimpleCountingIdlingResource(String resourceName) {
        this.mResourceName = resourceName;
    }

    /**
     * Returns the name of the resources (used for logging and idempotency  of registration).
     */
    @Override
    public String getName() {
        return mResourceName;
    }

    /**
     * Returns {@code true} if resource is currently idle. Espresso will <b>always</b> call this
     * method from the main thread, therefore it should be non-blocking and return immediately.
     */
    @Override
    public boolean isIdleNow() {
        return mCounter.get() == 0;
    }

    /**
     * Registers the given {@link ResourceCallback} with the resource. Espresso will call this method:
     * <ul>
     * <li>with its implementation of {@link ResourceCallback} so it can be notified asynchronously
     * that your resource is idle
     * <li>from the main thread, but you are free to execute the callback's onTransitionToIdle from
     * any thread
     * <li>once (when it is initially given a reference to your IdlingResource)
     * </ul>
     * <br>
     * You only need to call this upon transition from busy to idle - if the resource is already idle
     * when the method is called invoking the call back is optional and has no significant impact.
     *
     */
    @Override
    public void registerIdleTransitionCallback(ResourceCallback callback) {
        mResourceCallback = callback;
    }


    /**
     * Increments the count of in-flight transactions to the resource being monitored.
     */
    public void increment() {
        mCounter.getAndIncrement();
    }


    /**
     * Decrements the count of in-flight transactions to the resource being monitored.
     *
     * If this operation results in the counter falling below 0 - an exception is raised.
     *
     * @throws IllegalStateException if the counter is below 0.
     */
    public void decrement() {
        int counterVal = mCounter.decrementAndGet();
        if (counterVal == 0) {
            //
            // We're idle...tell espresso!
            if (null != mResourceCallback) {
                mResourceCallback.onTransitionToIdle();
            }
        }

        if (counterVal < 0) {
            throw new IllegalArgumentException("Counter has been corrupted!");
        }
    }
}
