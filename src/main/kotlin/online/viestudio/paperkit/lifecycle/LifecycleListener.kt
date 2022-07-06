package online.viestudio.paperkit.lifecycle

import online.viestudio.paperkit.plugin.KitPlugin

fun LifecycleListener.bindToPluginLifecycle(plugin: KitPlugin) {
    plugin.registerLifecycleListener(this)
}

interface LifecycleListener {

    suspend fun onStart() {}

    suspend fun onStop() {}
}