package com.thindie.avezer.engine

/**
 * Custom logging object to replace standard Android Log calls for better control and abstraction.
 */
object Log {
    @JvmStatic
    fun d(tag: String, message: String) {
        println(tag + message)
    }

    /**
     * Logs an error message at the ERROR level, printing it to logcat.
     * @param tag The log tag.
     * @param message The error message content to log.
     * @param throwable Optional exception associated with the error.
     */
    @JvmStatic
    fun e(tag: String, message: String, throwable: Throwable? = null) {
        if (throwable != null) {
           println(tag + " " + message + throwable.message)
        } else {
           println("$tag $message")
        }
    }

    /**
     * Logs a warning message at the WARN level, printing it to logcat.
     * @param tag The log tag.
     * @param message The warning message content to log.
     */
    @JvmStatic
    fun w(tag: String, message: String) {
        println("$tag $message")
    }
}
