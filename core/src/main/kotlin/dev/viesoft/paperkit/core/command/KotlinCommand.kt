@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package dev.viesoft.paperkit.core.command

import dev.viesoft.paperkit.core.plugin.IKotlinPlugin
import kotlinx.coroutines.launch
import org.bukkit.Location
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.SimpleCommandMap
import org.bukkit.entity.Player

abstract class KotlinCommand(
    protected val plugin: IKotlinPlugin,
    name: String,
    description: String = "",
    usageMessage: String = "/$name",
    aliases: List<String> = emptyList(),
) : Command(name, description, usageMessage, aliases) {

    protected val server get() = plugin.server

    final override fun execute(sender: CommandSender, commandLabel: String, args: Array<String>): Boolean {
        var isSuccessful = true
        plugin.scope.launch {
            isSuccessful = execute(sender, commandLabel, args.toList())
        }
        return isSuccessful
    }

    abstract suspend fun execute(sender: CommandSender, label: String, args: List<String>): Boolean

    final override fun tabComplete(
        sender: CommandSender,
        alias: String,
        args: Array<String>
    ): List<String> {
        val argsList = args.toList()
        var tabComplete: List<String>? = null
        plugin.scope.launch {
            tabComplete = tabComplete(sender, alias, argsList)
        }
        return tabComplete ?: defaultTabComplete(sender, argsList)
    }

    protected open suspend fun tabComplete(
        sender: CommandSender,
        alias: String,
        args: List<String>,
    ): List<String> = defaultTabComplete(sender, args)

    final override fun tabComplete(
        sender: CommandSender,
        alias: String,
        args: Array<String>,
        location: Location?
    ): List<String> {
        val argsList = args.toList()
        var tabComplete: List<String>? = null
        plugin.scope.launch {
            tabComplete = tabComplete(sender, alias, argsList, location)
        }
        return tabComplete ?: defaultTabComplete(sender, argsList)
    }

    protected open suspend fun tabComplete(
        sender: CommandSender,
        alias: String,
        args: List<String>,
        location: Location?
    ): List<String> = tabComplete(sender, alias, args)

    private fun defaultTabComplete(sender: CommandSender, args: List<String>): List<String> {
        val server = sender.server
        if (args.isEmpty() || !server.suggestPlayerNamesWhenNullTabCompletions()) return emptyList()
        val lastWord = args.last()
        val senderPlayer = sender as? Player
        return server.onlinePlayers.filter { player ->
            val isPlayerAcknowledgedBySender = senderPlayer == null || senderPlayer.canSee(player)
            isPlayerAcknowledgedBySender && player.name.startsWith(lastWord, ignoreCase = true)
        }.map { it.name }.sortedWith(String.CASE_INSENSITIVE_ORDER)
    }

    fun register(): Boolean {
        val isSuccessful = server.commandMap.register(plugin.name, this)
        if (isSuccessful) {
            server.onlinePlayers.forEach { it.updateCommands() }
        }
        return isSuccessful
    }

    fun unregister(): Boolean {
        val simpleCommandMap = server.commandMap as? SimpleCommandMap ?: return false
        val knownCommands = simpleCommandMap.knownCommands
        val keysForRemoval = knownCommands.filterValues { it === this }.keys
        return knownCommands.keys.removeAll(keysForRemoval)
    }
}
