package core

import core.ctx.AppContext
import core.commandsHandler.UpdateHandler
import org.telegram.telegrambots.api.methods.send.SendMessage
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageText
import org.telegram.telegrambots.api.objects.Update
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import java.lang.IllegalArgumentException

class Bot: TelegramLongPollingBot() {
    override fun getBotToken(): String =
            AppContext.settings["token"] ?: throw IllegalArgumentException("Need token")

    override fun getBotUsername(): String =
            AppContext.settings["name"] ?: throw IllegalArgumentException("Need name")


    override fun onUpdateReceived(update: Update?) {
        if (update != null) {
            println(update)
            UpdateHandler.handle(update)
        }
    }

    fun send(message: SendMessage) {
        println(message)
        execute(message)
    }

    fun editMessage(editMessageText: EditMessageText) {
        println(editMessageText)
        execute(editMessageText)
    }
}