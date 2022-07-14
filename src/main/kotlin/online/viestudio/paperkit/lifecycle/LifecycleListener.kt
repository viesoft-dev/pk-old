package online.viestudio.paperkit.lifecycle

import kotlinx.coroutines.launch
import online.viestudio.paperkit.koin.pluginQualifier
import online.viestudio.paperkit.plugin.KitPlugin
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

fun LifecycleListener.bindToPluginLifecycle() = bindToPluginLifecycle(get(pluginQualifier))

fun LifecycleListener.bindToPluginLifecycle(plugin: KitPlugin) = with(plugin) {
    scope.launch { registerLifecycleListener(this@bindToPluginLifecycle) }
}

interface LifecycleListener : KoinComponent {

    suspend fun onStart() {}

    suspend fun onStop() {}
}