# PaperKit Core

This module contains only the Kotlin implementation of Paper components, that provide you with fearless
experience.

### Example

```kotlin
class YourPlugin : KotlinPlugin() {

    override suspend fun loadConfig() {
        saveDefaultConfig()
        
        log.info { "Configuration of my plugin loaded." }
    }

    override suspend fun onEnabled() {
        getCommand("yourCommand")?.apply {
            setKotlinExecutor { sender, command, label, args ->
                sender.sendMessage("Executed!")
                true
            }
            setKotlinTabCompleter { sender, command, label, args ->
                emptyList()
            }
        }

        log.info { "My plugin enabled." }
    }

    override suspend fun onDisabled() {
        log.info { "My plugin disabled." }
    }
}

```

### Components

- [x] KotlinPlugin — alternative to JavaPlugin with suspend functions.
- [x] KotlinListener — alternative to Listener with suspend listeners support.
- [x] KotlinCommandExecutor — alternative to CommandExecutor with suspend execute method.
- [x] KotlinTabCompleter — alternative to TabCompleter with suspend tabComplete method.
