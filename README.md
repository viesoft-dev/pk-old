![Banner](.github/banner.png)

# PaperKit — framework that helps you develop Paper plugins fast and easy.

![Version badge](https://img.shields.io/static/v1?label=Version&message=1.0.0&color=red&style=for-the-badge)

Framework based on Paper api to build awesome plugins easy to extend and support.
Turns plugins development to convenient and fast process.
Currently, is under active development process, so isn't recommended to use in production yet.
PaperKit works as independent plugin on server, so you do not need to include it into every plugin and make it heavy.

### Advantages of using PaperKit:

- Fully support of coroutines
- Easy-To-Extend structure
- Fast development
- Convenient config system
- High-customisable
- Easy to interact between plugins
- Powerful tools out of box

## Config

PaperKit provides itself feel of config management.
To declare a config you need to override `declareConfiguration` method in your plugin class.

##### declareConfiguration — for declaring config management logic.

```kotlin
override fun ConfigurationDeclaration.declareConfiguration() {
    Config::class loadFrom (file("config.yml") or resource("config.yml") or defaults(Config::class))
}
```

The right part is named config sources. Count of sources is unlimited, but usually you have to use not more than 3 per
config.
The order in which you declare the resources is matter. At this example, you can see the next order
`file < resource < defaults`.
This way if the file doesn't exist it will create it before loading using the resource file, or defaults* if the
resource file doesn't exist as well.

##### Sources

- `file` references a file in your plugin directory `plugins/YourPlugin/config.yml`.
- `resource` references a resource in your plugin jar `plugins/YourPlugin.jar/config.yml`.
- `defaults` references the default config class constructor `Config()`.

This way you can provide a config file in your resources, if you would like. But PaperKit supports another better thing.

```kotlin
@Serializable
data class Config(
    @Comment(
        """
            Enter your hello world message here!
            Please, don't abuse your abilities ;)
        """
    )
    val helloWorld: Message = message("Hello, world!"),
)
```

You can define everything related to config just in your class. Here we go deeper in the `defaults` source.
Please, take a look at the `@Serializable`, it's required annotation for all config classes you'll create.
So, what's the default? As you may know, Kotlin support default values for parameters in constructor.
And it's the wonderful thing that makes happy a lot of developers.
PaperKit uses it to provide a file configuration file for user from a class.
No more need to check out did you declared the new property in default config, or no. Just do it in the class instead.

In result, the code above will look like this yml configuration:

```yaml
# Enter your hello world message here!
# Please, don't abuse your abilities ;)
helloWorld: "Hello, world!"
```

Also, there's an important functionality you need to know about. Did you ever wondering about update your plugin config?
How to add new fields since new version, edit some comments, etc.
PaperKit does it for you. If you'll add a new field, then it will be automatically added to an existent config from its
parent. It's named config merging. When the parent merges into the child config (note: it works only when the first
source is file and there's another valid source) it also processes comments equality, and if a comment was changed, then
it updates it in the actual config.

##### How to get the config?

```kotlin
private val config by config<YourConfig>()
```

It's very easy because PaperKit uses dependency injection. For further information on this
see [dependency injection in PaperKit](#dependency-injection).

## Commands

PaperKit has a beautiful and convenient command structure. You don't need to interact with scary bukkit-api anymore.
To register a new command, no more required declare it in the `plugin.yml` config, you can just register it in your
code. To do that, just override `declareCommands` method in your plugin class.

##### declareCommands — for declaring command management logic.

```kotlin
override fun CommandsDeclaration.declareCommands() {
    register { PaperKitCommand() }
}
```

Let's take a look into `PaperKitCommand` and its structure.

```kotlin
class PaperKitCommand : ParentCommand(
    name = "kit",
    description = "Main command of Paper-Kit framework.",
    aliases = listOf("kt"),
    permission = "paper-kit.execute",
    subCommands = listOf(ReloadCommand())
)
```

PaperKit command structure splits into two parts. Parent and child commands.
They both are implementing `BaseKitCommand`, so the difference is only in default set up.
Parent command is supposed to hold children commands and nothing more.
It doesn't mean that child command can't do that, but it means that parent commands already have everything done to go
with the task.

Parent command provides help that describes its child commands, whenever child command doesn't. Hope, you get the idea,
so let's move to the child command.

```kotlin
class ReloadCommand : ChildCommand(
    name = "reload",
    description = "An example child command",
    permission = "example.execute",
)
```

Now you can see the both commands are very similarly. But the child command also have body, where its logic is
implemented.

##### onExecute — for command logic.

```kotlin
override suspend fun onExecute(sender: CommandSender, args: Arguments): Boolean {
    sender.message {
        content("Hello, ${args.name}!")
        color(theme.primary)
    }
    return true
}
```

Here you implement your command logic.
No permission check is required, it's already done for you, as well.
Then, if the command requires any arguments for its executing, you have to override a special method for that.

```kotlin
override fun ArgumentsDeclaration.declareArguments() {
    argument {
        name("your name")
        description("The command requires your name!")
        required()
        validator { _, input ->
            when (input) {
                "vie10" -> "You are not vie10!"
                else -> null
            }
        }
        completer { _, _ -> listOf("vie10", "another") }
    }
}
```

And this reflection the power of PaperKit command system.
To add an argument you use the declaration method named `argument`, then you have to provide further information about
this argument.

- `name`, `description`, `isRequired` will be used in the command help to provide using information for player.
- `validator` validates arguments of the command, and returns an error message, or null if argument is ok.
- `completer` provides tab completion for the argument.

## Dependency injection

PaperKit is built with [Koin](https://insert-koin.io) in core, so you can use all its abilities in your plugins.
To provide a dependency you need to override `export` method in your plugin.

##### export — for exporting your dependencies.

```kotlin
override fun KoinModulesContainer.export() {
    module {
        single { DatabaseImpl() } bind Database::class
    }
}
```

For further information about dependency injection you may check out
the [Koin Documentation](https://insert-koin.io/docs/reference/introduction).

## How to start?

An example how to build a PaperKit plugin you can find out [here](https://github.com/vie10/live-config).

PaperKit is publishing into JitPack repository.
Add the dependency below into your build.gradle file to interact with PaperKit api.
Also, you need to declare `PaperKit` as dependency in your `plugin.yml`.
Do not include PaperKit api in your plugin, as well, as the Kotlin stdlib.

### [PaperKit Intellij Idea plugin](https://github.com/paper-kit/idea-paper-kit)

Check out the plugin for Intellij Idea that will make the process to start a new PaperKit project really easy.

#### Gradle

```kotlin
plugins {
    kotlin("plugin.serialization") version "1.6.21"
}

repositories {
    maven("https://jitpack.io")
    maven("https://papermc.io/repo/repository/maven-public/") // PaperApi is included into PaperKit dependency
}

dependencies {
    compileOnly("com.github.paper-kit", "paper-kit", "latest-version")
    // PaperKit provides all kotlin stuff.
    compileOnly(kotlin("stdlib"))
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.apply {
        jvmTarget = "17"
    }
}
```