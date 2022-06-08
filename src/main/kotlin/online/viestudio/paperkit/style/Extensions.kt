package online.viestudio.paperkit.style

import online.viestudio.paperkit.adventure.appendText
import online.viestudio.paperkit.adventure.showTextOnHover
import online.viestudio.paperkit.adventure.text
import online.viestudio.paperkit.command.KitCommand

fun KitCommand.buildBeautifulHelp() = text {
    color(appearance.primary)
    content(name)
    showTextOnHover {
        color(appearance.accent)
        content(description)
    }
    declaredArguments.forEach {
        val notation = if (it.isRequired) "" else "?"

        appendText {
            color(appearance.accent)
            content(" <$notation${it.name}>")
            showTextOnHover {
                color(appearance.accent)
                content(it.description)
            }
        }
    }
}