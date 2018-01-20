package core.commandsHandler.commands

import core.commandsHandler.*
import core.ctx.AppContext
import core.services.reminder.RemindEntity
import core.services.reminder.RepeatType
import org.telegram.telegrambots.api.methods.updatingmessages.DeleteMessage
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageText
import org.telegram.telegrambots.api.objects.Update
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class ReminderCommand : Command {
    enum class Waiting {
        TEXT,
        DATE,
        ASK_REPEAT,
        SET_REPEAT_TYPE
    }

    private var stage = Waiting.TEXT
    private val reminderBuilder = ReminderBuilder()

    class ReminderBuilder {
        var text: String? = null
        var time = ZonedDateTime.now().minusMinutes(LocalDateTime.now().minute.toLong())!!
        var chatId: Long? = null
        var userName: String? = null
        var repeatType: RepeatType? = null

        fun build(): RemindEntity = RemindEntity(
                _id = null,
                text = text!!,
                time = time.toInstant().toEpochMilli(),
                chatId = chatId!!,
                userName = userName,
                repeatType = repeatType)
    }


    override fun start(update: Update) {
        update.sendText("Что тебе напомнить?")
        with(reminderBuilder) {
            chatId = update.chatId()
            userName = update.message.from.userName ?: null
        }
    }

    override fun update(update: Update) {
        when (stage) {
            Waiting.TEXT -> {
                reminderBuilder.text = update.message.text
                askDate(update)
            }
            else -> throw IllegalStateException("Unknown stage")
        }
    }

    private val dateKeyboard = listOf(
            mapOf("year+" to "year+",
                    "year-" to "year-"),
            mapOf("month+" to "month+",
                    "month-" to "month-"),
            mapOf("day+" to "day+",
                    "day-" to "day-"),
            mapOf("hour+" to "hour+",
                    "hour-" to "hour-"),
            mapOf("min+" to "minutes+",
                    "min-" to "minutes-"),
            mapOf("ok" to "ok")
    )

    private val yesNoKeyboard = listOf(
            mapOf("да" to "yes",
                    "нет" to "no")
    )

    private val repeatKeyboard = listOf(
            mapOf("Каждый день" to "day",
                    "Каждую неделю" to "week",
                    "Раз в две недели" to "week2"),
            mapOf("Каждый месяц" to "month",
                    "Каждый год" to "year")
    )

    private fun askDate(update: Update) {
        stage = Waiting.DATE
        update.sendKeyboard(reminderBuilder.time.text(), createKeyboard(dateKeyboard))
    }


    override fun callback(update: Update) {
        when (stage) {
            Waiting.DATE -> {
                with(reminderBuilder) {
                    when (update.callbackQuery.data) {
                        "year+" -> time = time.plusYears(1)
                        "year-" -> time = time.minusYears(1)
                        "month+" -> time = time.plusMonths(1)
                        "month-" -> time = time.minusMonths(1)
                        "day+" -> time = time.plusDays(1)
                        "day-" -> time = time.minusDays(1)
                        "hour+" -> time = time.plusHours(1)
                        "hour-" -> time = time.minusHours(1)
                        "minutes+" -> time = time.plusMinutes(10)
                        "minutes-" -> time = time.minusMinutes(10)
                        "ok" -> {
                            AppContext.bot.execute(DeleteMessage(update.chatId(), update.callbackQuery.message.messageId))
                            stage = Waiting.ASK_REPEAT
                            update.sendKeyboard("Ок, напомню ${time.text()},\n надо повторять это напоминание?", createKeyboard(yesNoKeyboard))
                            return
                        }
                    }
                }
                val editMessage = EditMessageText()
                editMessage.chatId = update.chatId().toString()
                editMessage.text = reminderBuilder.time.text()
                editMessage.messageId = update.callbackQuery.message.messageId
                editMessage.replyMarkup = createKeyboard(dateKeyboard)
                AppContext.bot.editMessage(editMessage)
            }
            Waiting.ASK_REPEAT -> {
                with(reminderBuilder) {
                    when (update.callbackQuery.data) {
                        "yes" -> {
                            stage = Waiting.SET_REPEAT_TYPE
                            update.sendKeyboard("Как часто?", createKeyboard(repeatKeyboard))
                        }
                        "no" -> {
                            repeatType = RepeatType.NEVER
                            update.sendText("Ок, напомню ${reminderBuilder.time.text()}")
                            finish(update)
                        }
                        else -> throw IllegalStateException("Unknown answer")
                    }
                }
                AppContext.bot.execute(DeleteMessage(update.chatId(), update.callbackQuery.message.messageId))

            }
            Waiting.SET_REPEAT_TYPE -> {
                try {
                    reminderBuilder.repeatType = RepeatType.valueOf(update.callbackQuery.data.toUpperCase())
                    update.sendText("Ок, ${reminderBuilder.time.text()} и потом ${reminderBuilder.repeatType!!.text}")
                    finish(update)
                    AppContext.bot.execute(DeleteMessage(update.chatId(), update.callbackQuery.message.messageId))
                }
                catch (e: Exception) {
                    e.printStackTrace()
                    update.sendText("Извини, что-то пошло не так")
                }
            }
            else -> throw IllegalStateException("Unknown stage")
        }
    }

    private fun finish(update: Update) {
        AppContext.reminderService.addReminder(reminderBuilder.build())
        update.finishContext()
    }

    private fun ZonedDateTime.text(): String = this.format(DateTimeFormatter.ofPattern("uuuu MMM dd HH:mm"))
}