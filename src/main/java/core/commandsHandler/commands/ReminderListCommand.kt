package core.commandsHandler.commands

import core.commandsHandler.*
import core.ctx.AppContext
import core.services.reminder.RemindEntity
import core.services.reminder.RepeatType
import core.services.reminder.RepeatType.*
import core.utils.toDateTime
import org.telegram.telegrambots.api.methods.updatingmessages.DeleteMessage
import org.telegram.telegrambots.api.objects.Update
import java.time.format.DateTimeFormatter

class ReminderListCommand : Command {
    enum class Stage {
        LIST,
        CHOOSE_REMINDER,
        EDIT
    }

    private var stage = Stage.LIST
    private var page: MutableMap<Int, RemindEntity>? = null
    private var chosenPage = 0
    private fun firstItem() = chosenPage * 5
    private var chosenReminder: RemindEntity? = null

    override fun start(update: Update) {
        showList(update)
        stage = Stage.CHOOSE_REMINDER
    }

    override fun update(update: Update) {
        println(update)
    }

    override fun callback(update: Update) {
        AppContext.bot.execute(DeleteMessage(update.chatId(), update.callbackQuery.message.messageId))
        when (update.callbackQuery.data) {
            "<" -> {
                chosenPage--
                showList(update)
            }
            ">" -> {
                chosenPage++
                showList(update)
            }
            "delete" -> {
                AppContext.reminderService.delete(chosenReminder!!)
                showList(update)
            }
            else -> {
                val number = update.callbackQuery.data.toInt()
                chosenReminder = page!![number]!!
                showReminder(chosenReminder!!, update)
            }
        }
    }

    private fun showList(update: Update) {
        val reminders = AppContext.reminderService.fetchReminders(update.chatId()).toList()
        if (reminders.isEmpty()) {
            update.sendText("Нет напоминаний")
            return
        }
        if (reminders.size <= firstItem()) {
            if (chosenPage > 0) {
                chosenPage--
            }
        }

        var n = 1
        page = mutableMapOf()
        val keyBoard = mutableMapOf<String, String>()
        val textBuilder = StringBuilder()
        val lastItem = if (firstItem() + 5 > reminders.size) {
            reminders.size - 1
        } else {
            firstItem() + 4
        }
        if (firstItem() > 0) {
            keyBoard.put("<", "<")
        }
        for (i in firstItem()..lastItem) {
            page!!.put(n, reminders[i])
            keyBoard.put("$n", "$n")
            textBuilder.append(
                    "$n. ${reminders[i].text} " +
                            "${reminders[i].time
                                    .toDateTime()
                                    .format(DateTimeFormatter.ofPattern("HH:mm EEE dd/MM/yyyy"))} " +
                            "${reminders[i].repeatType?.text ?: ""}\n")
            n++
        }
        if (firstItem() + 5 < reminders.size) {
            keyBoard.put(">", ">")
        }

        update.sendKeyboard(textBuilder.toString(), createKeyboard(listOf(keyBoard)))
    }

    private fun showReminder(remindEntity: RemindEntity, update: Update) {
        update.sendKeyboard(remindEntity.text, createKeyboard(listOf(mapOf("удалить" to "delete"))))
    }

}