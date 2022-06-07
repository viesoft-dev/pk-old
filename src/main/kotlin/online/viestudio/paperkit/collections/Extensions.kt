package online.viestudio.paperkit.collections

import java.util.*
import java.util.concurrent.ConcurrentHashMap

fun <T> concurrentSetOf(vararg elements: T): MutableSet<T> =
    Collections.newSetFromMap<T>(ConcurrentHashMap()).apply { addAll(elements) }