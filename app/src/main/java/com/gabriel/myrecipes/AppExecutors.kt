package com.gabriel.myrecipes

import android.os.Handler
import android.os.Looper
import java.util.concurrent.Executor
import java.util.concurrent.Executors

/**
 * Global executor pools for the whole application.
 *
 * Grouping tasks like this avoids the effects of task starvation (e.g. disk reads don't wait behind
 * webservice requests).
 */
open class AppExecutors(
    val mDiskIO: Executor = Executors.newSingleThreadExecutor(),
    val mainThreadExecutor: Executor
) {

    constructor() : this(
        Executors.newSingleThreadExecutor(),
        MainThreadExecutor()
    )


    private class MainThreadExecutor : Executor {
        private val mainThreadHandler = Handler(Looper.getMainLooper())
        override fun execute(command: Runnable) {
            mainThreadHandler.post(command)
        }
    }

}



