package online.viestudio.paperkit.config

class LambdaConfigLoader<T : Any>(
    private val load: () -> T,
) : ConfigLoader<T> {

    override lateinit var value: T

    override fun load() {
        value = load.invoke()
    }
}