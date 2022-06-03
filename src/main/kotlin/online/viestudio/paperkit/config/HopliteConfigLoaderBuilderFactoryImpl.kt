package online.viestudio.paperkit.config

import com.sksamuel.hoplite.ConfigLoaderBuilder

internal class HopliteConfigLoaderBuilderFactoryImpl : HopliteConfigLoaderBuilderFactory {

    override fun create(): ConfigLoaderBuilder = ConfigLoaderBuilder.empty()
        .withClassLoader(this::class.java.classLoader)
        .addDefaults()
}