package online.viestudio.paperkit.config

interface ConfigLoader<T : Any> {

    val value: T

    fun load()
}