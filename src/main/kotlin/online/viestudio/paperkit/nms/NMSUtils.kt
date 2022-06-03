package online.viestudio.paperkit.nms

import org.bukkit.Server

val Server.nmsVersion: String get() = javaClass.getPackage().name.replace(".", ",").split(",")[3]