package online.viestudio.paperkit.plugin.exception

import online.viestudio.paperkit.plugin.KitPlugin

sealed class PluginException(override val message: String? = null, override val cause: Throwable? = null) :
    Exception(message, cause) {
    abstract val plugin: KitPlugin
}
