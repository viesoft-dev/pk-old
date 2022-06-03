package online.viestudio.paperkit.logger

import java.util.logging.Level
import java.util.logging.Logger

class KitLogger(
    private val logger: Logger,
) {

    inline fun i(block: () -> Any) {
        info(block().toString())
    }

    fun info(message: String) {
        logger.log(Level.INFO, message)
    }

    inline fun w(throwable: Throwable? = null, block: () -> Any) {
        warn(block().toString(), throwable)
    }

    fun warn(message: String, throwable: Throwable?) {
        logger.log(Level.WARNING, message, throwable)
    }

    inline fun e(throwable: Throwable, block: () -> Any) {
        error(block().toString(), throwable)
    }

    fun error(message: String, throwable: Throwable) {
        logger.log(Level.SEVERE, message, throwable)
    }

    inline fun d(throwable: Throwable? = null, block: () -> Any) {
        debug(block().toString(), throwable)
    }

    fun debug(message: String, throwable: Throwable?) {
        logger.log(Level.FINE, message, throwable)
    }

    inline fun v(throwable: Throwable? = null, block: () -> Any) {
        verbose(block().toString(), throwable)
    }

    fun verbose(message: String, throwable: Throwable?) {
        logger.log(Level.FINEST, message, throwable)
    }
}