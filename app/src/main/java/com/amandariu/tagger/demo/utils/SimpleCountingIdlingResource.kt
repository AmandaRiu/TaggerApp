package com.amandariu.tagger.demo.utils

import android.support.test.espresso.IdlingResource
import android.support.test.espresso.IdlingResource.ResourceCallback
import java.util.concurrent.atomic.AtomicInteger

/**
 * An simple counter implementation of [IdlingResource] that determines idleness by
 * maintaining an internal counter. When the counter is 0 - it is considered to be idle, when it is
 * non-zero it is not idle. This is very similar to the way a [java.util.concurrent.Semaphore]
 * behaves.
 *
 * This class can then be used to wrap up operations that while in progress should block tests from
 * accessing the UI.
 */
internal class SimpleCountingIdlingResource
/**
 * Creates a SimpleCountingIdlingResource
 *
 * @param [mResourceName] the resource name this resource should report to Espresso.
 */
(private val mResourceName: String) : IdlingResource {
    private val mCounter = AtomicInteger(0)
    //
    // written from main thread, read from any thread.
    @Volatile private var mResourceCallback: IdlingResource.ResourceCallback? = null

    /**
     * Returns the name of the resources (used for logging and idempotency of registration).
     */
    override fun getName(): String {
        return mResourceName
    }

    /**
     * Returns `true` if resource is currently idle. Espresso will **always** call this
     * method from the main thread, therefore it should be non-blocking and return immediately.
     */
    override fun isIdleNow(): Boolean {
        return mCounter.get() == 0
    }

    /**
     * Registers the given [ResourceCallback] with the resource. Espresso will call this method:
     *
     *  * with its implementation of [ResourceCallback] so it can be notified asynchronously
     * that your resource is idle
     *  * from the main thread, but you are free to execute the callback's onTransitionToIdle from
     * any thread
     *  * once (when it is initially given a reference to your IdlingResource)
     *
     * <br></br>
     * You only need to call this upon transition from busy to idle - if the resource is already idle
     * when the method is called invoking the call back is optional and has no significant impact.
     *
     */
    override fun registerIdleTransitionCallback(callback: IdlingResource.ResourceCallback) {
        mResourceCallback = callback
    }

    /**
     * Increments the count of in-flight transactions to the resource being monitored.
     */
    fun increment() {
        mCounter.getAndIncrement()
    }

    /**
     * Decrements the count of in-flight transactions to the resource being monitored.
     * If this operation results in the counter falling below 0 - an exception is raised.
     *
     * @throws IllegalStateException if the counter is below 0.
     */
    fun decrement() {
        val counterVal = mCounter.decrementAndGet()
        if (counterVal == 0) {
            //
            // We're idle...tell espresso!
            if (null != mResourceCallback) {
                mResourceCallback!!.onTransitionToIdle()
            }
        }

        if (counterVal < 0) {
            throw IllegalArgumentException("Counter has been corrupted!")
        }
    }
}
