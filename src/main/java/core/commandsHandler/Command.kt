package core.commandsHandler

import org.telegram.telegrambots.api.objects.Update

interface Command {
    fun start(update: Update)
    fun update(update: Update) {}
    fun callback(update: Update) {}
}