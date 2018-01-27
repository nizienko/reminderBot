package core.commandsHandler

import core.ctx.AppContext
import org.telegram.telegrambots.api.methods.send.SendMessage
import org.telegram.telegrambots.api.objects.Update
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton
import java.lang.IllegalStateException

fun Update.sendText(text: String): Update {
    AppContext.bot.send(
            SendMessage(
                    this.chatId(),
                    text
            )
    )
    return this
}

fun Update.chatId(): Long = when {
    this.hasMessage() -> this.message.chatId
    this.hasCallbackQuery() -> this.callbackQuery.message.chatId
    else -> throw IllegalStateException("update not supported for $this")
}

fun Update.sendKeyboard(text: String, buttons: InlineKeyboardMarkup) {
    val message = SendMessage()
    message.chatId = this.chatId().toString()
    message.text = text
    message.replyMarkup = buttons
    AppContext.bot.send(message)
}


fun createKeyboard(buttons: List<Map<String, String>>): InlineKeyboardMarkup {
    val keyBoard = InlineKeyboardMarkup()
    buttons.forEach { row ->
        keyBoard.keyboard.add(row.map {
            val button = InlineKeyboardButton()
            button.text = it.key
            button.callbackData = it.value
            return@map button
        }.toList())
    }
    return keyBoard
}

fun Update.contextId(): String {
    val id = this.message?.from?.id ?: this.callbackQuery.from.id
    return "${this.chatId()}#$id"
}

fun Update.finishContext() {
    if (UpdateHandler.contexts[this.contextId()] != null) {
        UpdateHandler.contexts.remove(this.contextId())
    }
}