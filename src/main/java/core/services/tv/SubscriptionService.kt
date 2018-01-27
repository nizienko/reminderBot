package core.services.tv

import com.mongodb.client.FindIterable
import core.ctx.AppContext
import core.db.Storage
import core.utils.Scheduler
import org.bson.types.ObjectId
import org.litote.kmongo.deleteMany
import org.litote.kmongo.deleteOneById
import org.litote.kmongo.find
import org.litote.kmongo.getCollection
import org.telegram.telegrambots.api.methods.send.SendMessage

class SubscriptionService {
    init {
        Scheduler.runEvery(60000) {
            AppContext.tvProgram.getProgramsIn(60000 * 15)
                    .forEach(this::sendNotifications)
        }
    }

    fun addSubscription(chatId: Long, type: TVProgramType) =
            Storage.db.getCollection<Subscription>().insertOne(Subscription(
                    null,
                    chatId,
                    type.name,
                    mapOf()))

    fun delSubscription(chatId: Long, type: TVProgramType) =
            Storage.db.getCollection<Subscription>()
                    .deleteMany(filter(chatId, type.name))


    fun subscriptionExist(chatId: Long, type: TVProgramType): Boolean =
            loadSubscriptions(filter(chatId, type.name)).count() > 0

    private fun getSubscriptions(type: TVProgramType): FindIterable<Subscription> =
            Storage.db.getCollection<Subscription>()
                    .find("{ type: '$type'}")

    private fun loadSubscriptions(filter: String): FindIterable<Subscription> =
            Storage.db.getCollection<Subscription>().find(filter)

    private fun filter(chatId: Long, subscriptionType: String): String =
            "{chatId: $chatId, type: '$subscriptionType'}"


    private fun sendNotifications(item: TVProgramItem) {
        getSubscriptions(TVProgramType.valueOf(item.type)).forEach { subscription ->
            AppContext.bot.send(
                    SendMessage(
                            subscription.chatId,
                            "${item.channel}: ${item.title}"
                    )
            )
        }
        Storage.db.getCollection<TVProgramItem>()
                .deleteOneById(item._id!!)
    }
}

data class Subscription(
        val _id: ObjectId?,
        val chatId: Long,
        val type: String,
        val parameters: Map<String, String>)
