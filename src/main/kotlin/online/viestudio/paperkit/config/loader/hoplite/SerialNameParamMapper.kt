package online.viestudio.paperkit.config.loader.hoplite

import com.sksamuel.hoplite.ParameterMapper
import kotlinx.serialization.SerialName
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.staticFunctions

class SerialNameParamMapper : ParameterMapper {

    override fun map(param: KParameter, constructor: KFunction<Any>, kclass: KClass<*>): Set<String> {
        val name = "get${param.name!!.replaceFirstChar { it.uppercase() }}\$annotations"
        val annotationsFun = kclass.staticFunctions.find { it.name == name } ?: return emptySet()
        val annotation = annotationsFun.findAnnotation<SerialName>() ?: return emptySet()
        return setOf(annotation.value)
    }
}