package online.viestudio.paperkit.config

import com.sksamuel.hoplite.ConfigLoaderBuilder

interface HopliteConfigLoaderBuilderFactory {

    fun create(): ConfigLoaderBuilder
}