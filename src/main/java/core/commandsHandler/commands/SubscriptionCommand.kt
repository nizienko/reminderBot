package core.commandsHandler.commands

import core.commandsHandler.*
import core.ctx.AppContext
import core.services.tv.TVProgramType
import org.telegram.telegrambots.api.objects.Update

class SubscriptionCommand : Command {

    private val commands = mapOf<String, (type: TVProgramType, update: Update) -> Unit>(
            "show_status" to { type, update ->
                if (!AppContext.subscriptionService.subscriptionExist(update.chatId(), type)) {
                    update.sendKeyboard("Получать уведомления ${type.message}?",
                            core.commandsHandler.createKeyboard(
                                    listOf(mapOf("Подписаться" to "${type.name}:subscribe"))))
                } else {
                    update.sendKeyboard("Вы подписаны на уведомления ${type.message}",
                            core.commandsHandler.createKeyboard(
                                    listOf(mapOf("Отписаться" to "${type.name}:unsubscribe"))))
                }
            },
            "subscribe" to { type, update ->
                if (!AppContext.subscriptionService.subscriptionExist(update.chatId(), type)) {
                    AppContext.subscriptionService.addSubscription(update.chatId(), type)
                    update.sendText("Ок")
                } else {
                    update.sendText("Вы уже подписаны")
                }
            },
            "unsubscribe" to { type, update ->
                AppContext.subscriptionService.delSubscription(update.chatId(), type)
                update.sendText("Больше не будет уведомлений ${type.message}")
            }
    )

    private val subscriptionsKeyboard = createKeyboard(
            TVProgramType.values().map {
                mapOf(it.name.toLowerCase() to "${it.name}:show_status")
            }.toList()
    )

    override fun start(update: Update) {
        update.sendKeyboard("Что интересует?", subscriptionsKeyboard)
    }

    override fun callback(update: Update) {
        val type = TVProgramType.valueOf(update.callbackQuery.data.split(":")[0])
        val cmd = commands[update.callbackQuery.data.split(":")[1]]
        if (cmd != null) {
            cmd(type, update)
        } else {
            update.sendText("Что-то пошло не так")
        }
    }
}