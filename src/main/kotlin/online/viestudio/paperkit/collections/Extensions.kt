@file:Suppress("unused")

package online.viestudio.paperkit.collections

import io.ktor.util.collections.*
import java.util.concurrent.ConcurrentHashMap

fun <T : Any> concurrentSetOf(vararg elements: T): MutableSet<T> = ConcurrentSet<T>().apply { addAll(elements) }

fun <K, V> concurrentMapOf(): MutableMap<K, V> = ConcurrentHashMap()
