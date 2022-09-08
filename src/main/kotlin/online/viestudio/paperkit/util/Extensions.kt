@file:Suppress("unused")

package online.viestudio.paperkit.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlin.system.measureTimeMillis

suspend inline fun <T, R> Iterable<T>.mapParallel(crossinline transform: suspend (T) -> R): List<R> {
    return coroutineScope {
        map { async { transform(it) } }.map { it.await() }
    }
}

suspend inline fun <T> Iterable<T>.forEachParallel(crossinline block: suspend (T) -> Unit): Iterable<T> = apply {
    coroutineScope {
        forEachParallel(this, block)
    }
}

suspend inline fun <T> Iterable<T>.forEachParallel(
    parallelism: Int,
    crossinline block: suspend (T) -> Unit,
): Iterable<T> = apply {
    coroutineScope {
        forEachParallel(this, parallelism, block)
    }
}

suspend inline fun <T> Iterable<T>.forEachParallel(
    coroutineScope: CoroutineScope,
    crossinline block: suspend (T) -> Unit,
): Iterable<T> = apply {
    coroutineScope.launch {
        forEach {
            async { block(it) }.start()
        }
    }.join()
}

suspend inline fun <T> Iterable<T>.forEachParallel(
    coroutineScope: CoroutineScope,
    parallelism: Int,
    crossinline block: suspend (T) -> Unit,
): Iterable<T> = apply {
    val semaphore = Semaphore(parallelism)
    coroutineScope.launch {
        forEach {
            async {
                semaphore.withPermit { block(it) }
            }.start()
        }
    }.join()
}

fun splitLines(text: String) = text.split(lineSeparator)

inline fun safeRunWithMeasuring(block: () -> Unit): Result<Long> {
    val result: Result<Unit>
    val millis = measureTimeMillis {
        result = runCatching(block)
    }
    return result.map { millis }
}

fun <T : Any> Array<T>.sliceStart(count: Int): Array<T> = sliceArray(count..lastIndex)

val lineSeparator: String get() = System.lineSeparator()
