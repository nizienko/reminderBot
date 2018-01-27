package core.commandsHandler.commands

import core.commandsHandler.Command
import core.commandsHandler.sendText
import core.commandsHandler.finishContext
import org.telegram.telegrambots.api.objects.Update

class HelpCommand : Command {
    override fun start(update: Update) {
        update.sendText("Пока у нас тут две команды:\n" +
                "/notify - нотификация о трансляциях спортивных событий по телеку\n" +
                "/remind - напоминалка, можно попросить бота прислать тебе сообщение в определенное время").finishContext()
    }
}