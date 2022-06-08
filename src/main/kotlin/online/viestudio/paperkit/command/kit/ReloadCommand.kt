package online.viestudio.paperkit.command.kit

import kotlinx.coroutines.async
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withContext
import online.viestudio.paperkit.adventure.message
import online.viestudio.paperkit.adventure.showTextOnHover
import online.viestudio.paperkit.command.Arguments
import online.viestudio.paperkit.command.ChildCommand
import online.viestudio.paperkit.koin.Global
import online.viestudio.paperkit.plugin.KitPlugin
import org.bukkit.command.CommandSender

class ReloadCommand : ChildCommand(
    name = "reload",
    description = "Reloads plugin or all if no plugin is specified.",
    permission = "paper-kit.reload",
) {

    private val allPlugins get() = Global.koin.getAll<KitPlugin>().toSet()

    override suspend fun onExecute(sender: CommandSender, args: Arguments): Boolean {
        val plugins = if (args.isEmpty()) {
            allPlugins
        } else listOf(allPlugins.first { it.name.equals(args[0], true) })
        sender.message {
            content("Reloading ${plugins.size} plugins..")
            color(appearance.primary)
        }
        supervisorScope {
            plugins.forEach {
                async {
                    withContext(it.context) {
                        it.reloadResources()
                    }
                }.start()
            }
        }
        sender.message {
            content("${plugins.size} plugins has been reloaded.")
            showTextOnHover {
                content(plugins.joinToString(", ") { it.name })
                color(appearance.accent)
            }
            color(appearance.primary)
        }
        return true
    }

    override fun ArgumentsDeclaration.declareArguments() {
        argument {
            name("plugin")
            description(
                """
                    Name of plugin that you would like to reload, or omit it, to reload all plugins.
                """.trimIndent()
            )
            completer { _, _ ->
                allPlugins.map { it.name }
            }
            validator { _, pluginName ->
                val plugin = allPlugins.find { it.name.equals(pluginName, true) }
                if (plugin == null) {
                    "Plugin not found"
                } else null
            }
        }
    }
}