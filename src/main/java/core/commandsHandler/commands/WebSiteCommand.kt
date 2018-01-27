package core.commandsHandler.commands

import core.ctx.AppContext
import core.commandsHandler.Command
import core.commandsHandler.sendText
import core.commandsHandler.finishContext
import org.jsoup.Jsoup
import org.telegram.telegrambots.api.methods.send.SendMessage
import org.telegram.telegrambots.api.objects.Update

class WebSiteCommand : Command {
    enum class State {
        START, WAIT_FOR_URL
    }

    var currentState = State.START

    override fun start(update: Update) {
        update.sendText("Какой url?")
        currentState = State.WAIT_FOR_URL
    }

    override fun update(update: Update) {
        when(currentState) {
            State.WAIT_FOR_URL -> {
                val text = Jsoup.connect(update.message.text.trim())
                        .get().body().text()
                AppContext.bot.send(
                        SendMessage(
                                update.message.chatId,
                                text
                        )
                )
                update.finishContext()
            }
            else -> {
            }
        }
    }
}