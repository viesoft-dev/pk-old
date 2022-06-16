package online.viestudio.paperkit.koin

import org.koin.core.module.Module
import org.koin.dsl.ModuleDeclaration
import org.koin.dsl.module as koinModule

class KoinModulesContainer {

    private val _modules: MutableList<Module> = ArrayList()
    val modules: List<Module> = _modules

    fun module(createdAtStart: Boolean = false, moduleDeclaration: ModuleDeclaration) {
        _modules.add(koinModule(createdAtStart, moduleDeclaration))
    }
}
