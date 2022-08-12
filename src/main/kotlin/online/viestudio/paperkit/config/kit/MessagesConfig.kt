package online.viestudio.paperkit.config.kit

import kotlinx.serialization.Serializable
import online.viestudio.paperkit.annotate.DeclareFileOrDefaultsConfig
import online.viestudio.paperkit.message.Message
import online.viestudio.paperkit.message.message
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

@Serializable
@DeclareFileOrDefaultsConfig("messages.yml")
internal data class MessagesConfig(
    val notEnoughPermissions: Message = message("<{{errorHex}}>You haven't enough permissions to do that."),
    val reloadingPlugins: Message = message("<{{primaryHex}}>Reloading <{{accentHex}}>{count} <{{primaryHex}}>plugins.."),
    val pluginsReloaded: Message = message("<hover:show_text:'<{{accentHex}}>{list}'><{{accentHex}}>{count} <{{primaryHex}}>plugins has been reloaded."),
    val commandNotWork: Message = message(
        """
            <{{errorHex}}>This command doesn't work properly.
            <{{errorHex}}>Please, contact the server administrator to resolve the problem.
        """.trimIndent()
    ),
) {

    internal companion object {

        val KoinComponent.messages get() = get<MessagesConfig>()
    }
}
