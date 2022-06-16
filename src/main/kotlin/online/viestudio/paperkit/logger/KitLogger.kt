package online.viestudio.paperkit.logger

import java.util.logging.Logger

class KitLogger(
    private val logger: Logger,
) {

    inline fun i(block: () -> Any) {
        info(block().toString())
    }

    fun info(message: String) {
        logger.info(message)
    }

    inline fun w(throwable: Throwable? = null, block: () -> Any) {
        warn(block().toString(), throwable)
    }

    fun warn(message: String, throwable: Throwable?) {
        logger.warning(message)
        throwable?.printStackTrace()
    }

    inline fun e(throwable: Throwable, block: () -> Any) {
        error(block().toString(), throwable)
    }

    fun error(message: String, throwable: Throwable) {
        logger.severe(message)
        throwable.printStackTrace()
    }

    inline fun d(throwable: Throwable? = null, block: () -> Any) {
        debug(block().toString(), throwable)
    }

    fun debug(message: String, throwable: Throwable?) {
        logger.info(message)
        throwable?.printStackTrace()
    }
}
