package online.viestudio.paperkit

import online.viestudio.paperkit.command.CommandsDeclaration
import online.viestudio.paperkit.command.kit.PaperKitCommand
import online.viestudio.paperkit.logger.KitLogger
import online.viestudio.paperkit.plugin.BaseKitPlugin

class PaperKitPlugin : BaseKitPlugin() {

    override suspend fun onStart() {
        log.banner()
    }

    private fun KitLogger.banner() = i {
        """
              ____                       _  _____ _____ 
             |  _ \ __ _ _ __   ___ _ __| |/ /_ _|_   _|
             | |_) / _` | '_ \ / _ \ '__| ' / | |  | |  
             |  __/ (_| | |_) |  __/ |  | . \ | |  | |  
             |_|   \__,_| .__/ \___|_|  |_|\_\___| |_|  
                        |_|
             
             GitHub: https://github.com/paper-kit/paper-kit/
             Version: $version
        """
    }

    override fun CommandsDeclaration.declareCommands() {
        register { PaperKitCommand() }
    }
}