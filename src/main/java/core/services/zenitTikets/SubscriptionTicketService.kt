package core.services.zenitTikets

import com.mongodb.client.FindIterable
import core.ctx.AppContext
import core.db.Storage
import org.bson.types.ObjectId
import org.litote.kmongo.deleteMany
import org.litote.kmongo.find
import org.litote.kmongo.getCollection
import org.telegram.telegrambots.api.methods.send.SendMessage

class SubscriptionTicketService {
    companion object {

        fun sendNotifications(item: Game) {
            getSubscriptions(KindOfSport.valueOf(item.sport)).forEach { subscription ->
                AppContext.bot.send(
                        SendMessage(
                                subscription.chatId,
                                item.toMessage()
                        )
                )
            }
        }

        fun getSubscriptions(sport: KindOfSport): FindIterable<TicketSubscription> =
                Storage.db.getCollection<TicketSubscription>()
                        .find("{ sport: '${sport.name}'}")

        fun addSubscription(chatId: Long, sport: KindOfSport) =
                Storage.db.getCollection<TicketSubscription>().insertOne(TicketSubscription(
                        null,
                        chatId,
                        sport.name,
                        mapOf()))

        fun delSubscription(chatId: Long, sport: KindOfSport) =
                Storage.db.getCollection<TicketSubscription>()
                        .deleteMany(filter(chatId, sport))


        fun subscriptionExist(chatId: Long, sport: KindOfSport): Boolean =
                loadSubscriptions(filter(chatId, sport)).count() > 0


        private fun loadSubscriptions(filter: String): FindIterable<TicketSubscription> =
                Storage.db.getCollection<TicketSubscription>().find(filter)

        private fun filter(chatId: Long, sport: KindOfSport): String =
                "{chatId: $chatId, type: '${sport.name}'}"

    }


}

data class TicketSubscription(
        val _id: ObjectId?,
        val chatId: Long,
        val type: String,
        val parameters: Map<String, String>)