package online.viestudio.paperkit.plugin.exception

import online.viestudio.paperkit.plugin.KitPlugin

data class InvalidPluginStateException(
    override val plugin: KitPlugin,
    val expectedStates: Set<KitPlugin.State>,
    val actualState: KitPlugin.State = plugin.state,
) : PluginException(
    """
        Plugin is in an invalid state.
        Expected state: ${expectedStates.joinToString(" or ")}.
        Actual state: ${actualState}.
        It's recommend to check state before invoking an action.
    """.trimIndent()
)