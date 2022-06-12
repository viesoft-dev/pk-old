package online.viestudio.paperkit.config.kit

import kotlinx.serialization.Serializable
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

@Serializable
internal data class TranslationConfig(
    val plugin: String = "plugin",
    val pluginNotFound: String = "Plugin not found",
) {

    internal companion object {

        val KoinComponent.translation get() = get<TranslationConfig>()
    }
}