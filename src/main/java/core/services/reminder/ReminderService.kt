package core.services.reminder

import core.ctx.AppContext
import core.db.Storage
import core.utils.Scheduler
import core.utils.toDateTime
import org.litote.kmongo.MongoOperator.lt
import org.litote.kmongo.deleteOneById
import org.litote.kmongo.find
import org.litote.kmongo.getCollection
import org.telegram.telegrambots.api.methods.send.SendMessage

class ReminderService {
    init {
        Scheduler.runEvery(60000) {
            check()
        }
    }

    private fun check() {
        val untilTime = System.currentTimeMillis() + 60000
        Storage.db.getCollection<RemindEntity>()
                .find("{time:{$lt: $untilTime}}").forEach {
            println(it)
            AppContext.bot.send(
                    SendMessage(it.chatId,
                            "${getUserNameStringIfExist(it.userName)} ${it.text}")
            )
            Storage.db.getCollection<RemindEntity>().deleteOneById(it._id!!)
            insertNext(it)
        }
    }

    private fun getUserNameStringIfExist(userName: String?) = if (userName != null) "@$userName" else ""

    fun addReminder(reminder: RemindEntity) {
        Storage.db.getCollection<RemindEntity>()
                .insertOne(reminder)
    }

    fun fetchReminders(chatId: Long): List<RemindEntity> =
            Storage.db.getCollection<RemindEntity>()
                    .find("{chatId: $chatId}").toList()

    fun delete(reminder: RemindEntity) =
            Storage.db.getCollection<RemindEntity>()
                    .deleteOneById(reminder._id!!)


    private fun insertNext(reminder: RemindEntity) = with(reminder) {
        when (repeatType) {
            RepeatType.DAY -> {
                Storage.db.getCollection<RemindEntity>()
                        .insertOne(RemindEntity(
                                text = text,
                                time = time + 60 * 1000 * 60 * 24,
                                chatId = chatId,
                                userName = userName,
                                repeatType = repeatType
                        ))
            }
            RepeatType.WEEK -> {
                Storage.db.getCollection<RemindEntity>()
                        .insertOne(RemindEntity(
                                text = text,
                                time = time + 60 * 1000 * 60 * 24 * 7,
                                chatId = chatId,
                                userName = userName,
                                repeatType = repeatType
                        ))
            }
            RepeatType.WEEK2 -> {
                Storage.db.getCollection<RemindEntity>()
                        .insertOne(RemindEntity(
                                text = text,
                                time = time + 60 * 1000 * 60 * 24 * 14,
                                chatId = chatId,
                                userName = userName,
                                repeatType = repeatType
                        ))
            }
            RepeatType.MONTH -> {
                val newTime = time.toDateTime().plusMonths(1)
                Storage.db.getCollection<RemindEntity>()
                        .insertOne(RemindEntity(
                                text = text,
                                time = newTime.toInstant().toEpochMilli(),
                                chatId = chatId,
                                userName = userName,
                                repeatType = repeatType
                        ))
            }
            RepeatType.YEAR -> {
                val newTime = time.toDateTime().plusYears(1)
                Storage.db.getCollection<RemindEntity>()
                        .insertOne(RemindEntity(
                                text = text,
                                time = newTime.toInstant().toEpochMilli(),
                                chatId = chatId,
                                userName = userName,
                                repeatType = repeatType
                        ))
            }
        }
    }
}