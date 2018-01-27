package core.commandsHandler.commands

import core.commandsHandler.Command
import org.telegram.telegrambots.api.objects.Update

object CommandsFactory {
    val commands = mapOf(
            "/notify" to { SubscriptionCommand() },
            "/web" to { WebSiteCommand() },
            "/button" to { ButtonCommand() },
            "/remind" to { ReminderCommand() },
            "/remind_list" to { ReminderListCommand() },
            "/help" to { HelpCommand() }
    )
}

private fun Update.cmd(): String = this.message.text.split(" ")[0].trim()

fun Update.isCommand(): Boolean = CommandsFactory.commands.containsKey(this.cmd())

fun Update.createCommand(): Command {
    if (CommandsFactory.commands.containsKey(this.cmd())) {
        return CommandsFactory.commands[this.cmd()]!!()
    }
    return CommandsFactory.commands["/help"]!!()
}
