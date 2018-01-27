package core.commandsHandler

import core.commandsHandler.commands.CommandsFactory
import core.commandsHandler.commands.createCommand
import core.commandsHandler.commands.isCommand
import org.telegram.telegrambots.api.objects.Update

object UpdateHandler {
    val contexts = mutableMapOf<String, Command>()

    fun handle(update: Update) {
        when {
            update.hasMessage() -> {
                when {
                    update.isCommand() -> {
                        val command = update.createCommand()
                        contexts.put(update.contextId(), command)
                        command.start(update)
                    }
                    contexts[update.contextId()] != null -> contexts[update.contextId()]!!.update(update)
                    else -> CommandsFactory.commands["/help"]!!().start(update)
                }
            }
            update.hasCallbackQuery() -> {
                when {
                    contexts[update.contextId()] != null -> contexts[update.contextId()]!!.callback(update)
                }
            }
        }
    }
}


