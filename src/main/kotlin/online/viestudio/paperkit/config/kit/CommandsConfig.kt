package online.viestudio.paperkit.config.kit

import kotlinx.serialization.Serializable
import online.viestudio.paperkit.annotate.DeclareFileOrDefaultsConfig
import online.viestudio.paperkit.command.CommandConfig
import online.viestudio.paperkit.command.argument.ArgumentConfig
import online.viestudio.paperkit.util.splitLines
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

@Serializable
@DeclareFileOrDefaultsConfig("commands.yml")
internal data class CommandsConfig(
    val paperKit: CommandConfig = CommandConfig(
        name = "kit",
        aliases = listOf("kt"),
        description = "Command to manage PaperKit plugin",
        permission = "paperkit.execute"
    ),
    val reload: CommandConfig = CommandConfig(
        name = "reload",
        description = "Reloads plugin or all if no specified.",
        permission = "paperkit.reload",
        arguments = mapOf(
            "plugin" to ArgumentConfig(
                name = "plugin",
                description = splitLines("Name of the plugin which you want to reload or \"all\" to reload all.")
            ),
        )
    ),
) {

    internal companion object {

        val KoinComponent.commandsConfig get() = get<CommandsConfig>()
    }
}
