package core.services.reminder

import org.bson.types.ObjectId

data class RemindEntity(
        val _id: ObjectId? = null,
        val text: String,
        val time: Long,
        val chatId: Long,
        val userName: String? = null,
        val repeatType: RepeatType?)

