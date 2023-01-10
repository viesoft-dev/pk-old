@file:Suppress("unused")

package online.viestudio.paperkit.collections

import java.util.*
import java.util.concurrent.ConcurrentHashMap

fun <T : Any> concurrentSetOf(vararg elements: T): MutableSet<T> {
    return Collections.newSetFromMap<T>(ConcurrentHashMap()).apply {
        addAll(elements)
    }
}

fun <K, V> concurrentMapOf(): MutableMap<K, V> = ConcurrentHashMap()
