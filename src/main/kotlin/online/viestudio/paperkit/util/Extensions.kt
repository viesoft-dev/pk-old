package online.viestudio.paperkit.util

import kotlin.system.measureTimeMillis

inline fun safeRunWithMeasuring(block: () -> Unit): Result<Long> {
    val result: Result<Unit>
    val millis = measureTimeMillis {
        result = runCatching(block)
    }
    return result.map { millis }
}

fun <T : Any> Array<T>.sliceStart(count: Int): Array<T> = sliceArray(count..lastIndex)

val lineSeparator: String get() = System.lineSeparator()