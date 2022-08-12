package online.viestudio.paperkit.command

import online.viestudio.paperkit.command.argument.Argument
import online.viestudio.paperkit.config.kit.MessagesConfig.Companion.messages
import online.viestudio.paperkit.koin.plugin
import online.viestudio.paperkit.logger.KitLogger
import online.viestudio.paperkit.message.message
import online.viestudio.paperkit.plugin.KitPlugin
import online.viestudio.paperkit.style.buildBeautifulHelp
import online.viestudio.paperkit.util.sliceStart
import org.bukkit.Server
import org.bukkit.command.CommandSender

@Suppress("MemberVisibilityCanBePrivate")
abstract class BaseKitCommand(
    override val subCommands: List<KitCommand> = emptyList(),
) : KitCommand {

    final override val declaredArguments: List<Argument> by lazy { ArgumentsDeclaration().apply { declareArguments() }.arguments }
    override val name: String get() = config.name
    override val aliases: List<String> get() = config.aliases.orEmpty()
    override val description: String get() = config.description
    override val permission: String get() = config.permission
    override val minArguments: Int by lazy { declaredArguments.count { it.isRequired } }
    override val appearance get() = plugin.appearance
    protected abstract val config: CommandConfig
    protected val plugin: KitPlugin by plugin()
    protected val server: Server get() = plugin.server
    protected val serverScope get() = plugin.serverScope
    protected val scope get() = plugin.scope
    protected val log get() = plugin.log
    private val subCommandNames: List<String> by lazy { subCommands.map { it.aliases + it.name }.flatten() }

    final override suspend fun ensureInit() {
        minArguments
        declaredArguments
        plugin
    }

    protected open fun ArgumentsDeclaration.declareArguments() {
        // Should not declare any arguments by default.
    }

    final override suspend fun execute(sender: CommandSender, args: Arguments) {
        if (!sender.hasPermission(permission)) {
            val result = runCatching { onHasNotPermission(sender, args) }.onFailure {
                log.logProblem(sender, args, it)
            }.getOrElse { false }
            if (!result) return
        }
        runIfSubcommandPresented(args) {
            execute(sender, args.sliceStart(1))
            return
        }
        if (!verifyArguments(sender, args)) return
        runCatching {
            onExecute(sender, args)
        }.onFailure {
            runCatching { onExecuteFailure(sender, args, it) }.onFailure { plugin.log.logProblem(sender, args, it) }
        }.onSuccess {
            if (!it) sender.sendHelp()
        }
    }

    protected open suspend fun onHasNotPermission(sender: CommandSender, args: Arguments): Boolean {
        sender.message(messages.notEnoughPermissions)
        return false
    }

    protected open suspend fun onExecuteFailure(sender: CommandSender, args: Arguments, e: Throwable) {
        sender.message(messages.commandNotWork)
        log.logProblem(sender, args, e)
    }

    private fun KitLogger.logProblem(sender: CommandSender, args: Arguments, e: Throwable) = w(e) {
        """
            Executing command $name failed.
            Sender: $sender.
            Arguments: ${args.joinToString(", ")}.
            Thread: ${Thread.currentThread()}.
            Error message: ${e.message}
            
            This problem is related to ${plugin.name} plugin, report it to the developers.
        """.trimIndent()
    }

    protected abstract suspend fun onExecute(sender: CommandSender, args: Arguments): Boolean

    protected open suspend fun CommandSender.sendHelp() {
        sendMessage(buildBeautifulHelp())
    }

    private suspend fun verifyArguments(sender: CommandSender, args: Arguments): Boolean {
        if (args.size < minArguments) {
            sender.sendHelp()
            return false
        }
        args.forEachIndexed { index, s ->
            val declaredArgument = declaredArguments.getOrNull(index) ?: return true
            val result = runCatching { declaredArgument.validator(args, s) }.getOrElse { "Plugin error" }
            if (!result.isNullOrEmpty()) {
                runCatching { onWrongArgument(sender, args, index, result) }.onFailure {
                    log.logProblem(
                        sender,
                        args,
                        it
                    )
                }
                return false
            }
        }
        return true
    }

    protected open suspend fun onWrongArgument(
        sender: CommandSender,
        args: Arguments,
        wrongArgumentIndex: Int,
        description: String,
    ) {
        with(appearance) {
            sender.message(
                """
                <$errorHex>$description
                <$primaryHex>$name ${
                    args.mapIndexed { index, s ->
                        if (index == wrongArgumentIndex) {
                            "<$errorHex>> $s \\<"
                        } else s
                    }.joinToString(" ")
                }
                """.trimIndent()
            )
        }
    }

    final override suspend fun complete(sender: CommandSender, args: Arguments): List<String> {
        if (!sender.hasPermission(permission)) return emptyList()
        runIfSubcommandPresented(args) {
            return complete(sender, args.sliceStart(1))
        }

        if (args.isEmpty()) return emptyList()
        val declaredArgument = declaredArguments.getOrNull(args.lastIndex) ?: return extraComplete(args)
        val complete = runCatching {
            declaredArgument.completer(args, args.last())
        }.onFailure { log.logProblem(sender, args, it) }.getOrElse { emptyList() }
        return complete + extraComplete(args)
    }

    private fun extraComplete(args: Arguments): List<String> {
        return if (args.size == 1) {
            subCommandNames
        } else emptyList()
    }

    private inline fun runIfSubcommandPresented(args: Arguments, block: KitCommand.() -> Unit) {
        if (args.isNotEmpty()) {
            val possibleName = args.first()
            val subCommand = subCommands.find { subCommand ->
                subCommand.name.equals(possibleName, true) || subCommand.aliases.any { it.equals(possibleName, true) }
            }
            subCommand?.block()
        }
    }

    class ArgumentsDeclaration {

        private val _arguments: MutableList<Argument> = mutableListOf()
        val arguments: List<Argument> = _arguments

        inline fun argument(block: Argument.Builder.() -> Unit) {
            addArgument(Argument.builder().apply(block).build())
        }

        fun addArgument(argument: Argument) {
            _arguments.add(argument)
        }
    }
}
