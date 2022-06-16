package online.viestudio.paperkit.config.loader

interface ConfigProvider<T> {

    fun provide(): T
}
