package core

import core.ctx.AppContext
import org.telegram.telegrambots.ApiContextInitializer
import org.telegram.telegrambots.TelegramBotsApi

fun main(args: Array<String>) {
    ApiContextInitializer.init()
    val telegramApi = TelegramBotsApi()
    telegramApi.registerBot(AppContext.bot)
}