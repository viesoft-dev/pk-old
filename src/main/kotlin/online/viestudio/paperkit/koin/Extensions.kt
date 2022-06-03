package online.viestudio.paperkit.koin

import online.viestudio.paperkit.plugin.KitPlugin
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier
import org.koin.core.qualifier.StringQualifier
import kotlin.properties.ReadOnlyProperty

inline fun <reified T : Any> KoinComponent.pluginConfig(
    qualifier: Qualifier? = null,
    noinline parameters: ParametersDefinition? = null,
): ReadOnlyProperty<Any?, T> = ReadOnlyProperty { _, _ -> get(qualifier, parameters) }

fun KoinComponent.plugin(
    name: String,
    mode: LazyThreadSafetyMode = LazyThreadSafetyMode.NONE,
    parameters: ParametersDefinition? = null,
): Lazy<KitPlugin> = lazy(mode) { get(StringQualifier(name), parameters) }