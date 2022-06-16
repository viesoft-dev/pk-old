package online.viestudio.paperkit.command.kit

import kotlinx.coroutines.async
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withContext
import online.viestudio.paperkit.command.Arguments
import online.viestudio.paperkit.command.ChildCommand
import online.viestudio.paperkit.config.kit.CommandsConfig.Companion.commandsConfig
import online.viestudio.paperkit.config.kit.MessagesConfig.Companion.messages
import online.viestudio.paperkit.config.kit.TranslationConfig.Companion.translation
import online.viestudio.paperkit.koin.Global
import online.viestudio.paperkit.message.message
import online.viestudio.paperkit.plugin.KitPlugin
import org.bukkit.command.CommandSender
import java.util.Collections.singleton

internal class ReloadCommand : ChildCommand() {

    override val config get() = commandsConfig.reload
    private val allPlugins get() = Global.koin.getAll<KitPlugin>().toSet()
    private val Arguments.plugin: String get() = this[0]

    override suspend fun onExecute(sender: CommandSender, args: Arguments): Boolean {
        val plugins = if (args.plugin.equals("all", true)) {
            allPlugins
        } else singleton(findPluginByName(args.plugin))
        sender.message(messages.reloadingPlugins, "count" to plugins.size)
        supervisorScope {
            plugins.forEach {
                async {
                    withContext(it.context) {
                        it.reloadResources()
                    }
                }.start()
            }
        }
        sender.message(
            messages.pluginsReloaded,
            "count" to plugins.size,
            "list" to plugins.joinToString(", ") { it.name }
        )
        return true
    }

    private fun findPluginByName(name: String) = allPlugins.first { it.name.equals(name, true) }

    override fun ArgumentsDeclaration.declareArguments() {
        argument {
            config { config.argument("plugin") }
            completer { _, _ ->
                allPlugins.map { it.name }.toMutableList().apply { add("all") }
            }
            validator { _, pluginName ->
                if (pluginName.equals("all", true)) return@validator null
                val plugin = allPlugins.find { it.name.equals(pluginName, true) }
                if (plugin == null) {
                    translation.pluginNotFound
                } else null
            }
        }
    }
}
