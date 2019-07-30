package com.gabriel.myrecipes.util

/**
 * A generic class that holds a value with its loading status.
 * @param <T>
</T> */
data class ResourceData<out T>(val status: Status, val data: T?, val message: String?) {
    companion object {
        fun <T> success(data: T?): ResourceData<T> {
            return ResourceData(Status.SUCCESS, data, null)
        }

        fun <T> error(msg: String, data: T?): ResourceData<T> {
            return ResourceData(Status.ERROR, data, msg)
        }

        fun <T> loading(data: T?): ResourceData<T> {
            return ResourceData(Status.LOADING, data, null)
        }

        fun <T> exhausted(data: T?): ResourceData<T> {
            return ResourceData(Status.EXHAUSTED, data, null)
        }
    }

    enum class Status {
        SUCCESS,
        ERROR,
        LOADING,
        EXHAUSTED
    }
}

/**
 * Status of a resource that is provided to the UI.
 *
 *
 * These are usually created by the Repository classes where they return
 * `LiveData<ResourceData<T>>` to pass back the latest data to the UI with its fetch status.
 */

