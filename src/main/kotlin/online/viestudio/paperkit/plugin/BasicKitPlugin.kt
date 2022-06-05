package online.viestudio.paperkit.plugin

import online.viestudio.paperkit.logger.KitLogger
import online.viestudio.paperkit.plugin.KitPlugin.State
import org.bukkit.plugin.java.JavaPlugin

/**
 * This abstraction is supposed to implement all the basic functionality of KitPlugin interface.
 */
abstract class BasicKitPlugin : JavaPlugin(), KitPlugin {

    /**
     * State of the plugin.
     * @see State
     */
    final override var state: State = State.Stopped

    /**
     * Version of the plugin.
     */
    final override val version: String get() = description.version

    /**
     * Logger of the plugin.
     */
    final override val log: KitLogger by lazy { KitLogger(logger) }

    final override fun toString(): String = "Plugin $name v. $version powered by (c) PaperKit"
}