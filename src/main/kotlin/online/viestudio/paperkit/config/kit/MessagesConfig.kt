package online.viestudio.paperkit.config.kit

import kotlinx.serialization.Serializable
import online.viestudio.paperkit.message.message
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

@Serializable
internal data class MessagesConfig(
    val notEnoughPermissions: List<String> = message(
        "<{{errorHex}}>You haven't enough permissions to do that."
    ),
    val reloadingPlugins: List<String> = message(
        "<{{primaryHex}}>Reloading <{{accentHex}}>{count} <{{primaryHex}}>plugins.."
    ),
    val pluginsReloaded: List<String> = message(
        "<hover:show_text:'<{{accentHex}}>{list}'><{{accentHex}}>{count} <{{primaryHex}}>plugins has been reloaded."
    ),
) {

    internal companion object {

        val KoinComponent.messages get() = get<MessagesConfig>()
    }
}