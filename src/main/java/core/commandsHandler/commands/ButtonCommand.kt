package core.commandsHandler.commands

import core.commandsHandler.Command
import core.commandsHandler.createKeyboard
import core.commandsHandler.sendKeyboard
import core.commandsHandler.sendText
import org.telegram.telegrambots.api.objects.Update

class ButtonCommand : Command {
    override fun start(update: Update) {
        update.sendKeyboard("Choose", createKeyboard(listOf(mapOf(
                "button1" to "data1",
                "button2" to "data2"
        ))))
    }

    override fun callback(update: Update) {
        update.sendText(update.callbackQuery.data)
    }
}