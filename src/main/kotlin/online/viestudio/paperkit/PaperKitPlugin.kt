package online.viestudio.paperkit

import online.viestudio.paperkit.annotate.PluginStartHook
import online.viestudio.paperkit.plugin.AnnotationDrivenPlugin
import online.viestudio.paperkit.plugin.KitPlugin


@PluginStartHook
internal fun KitPlugin.banner() = log.i {
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

internal class PaperKitPlugin : AnnotationDrivenPlugin()
