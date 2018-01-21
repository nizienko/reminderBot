package services

import core.db.Storage
import core.services.tv.Channel
import core.services.tv.TVProgramItem
import core.services.zenitTikets.ZenitTicketService
import org.junit.jupiter.api.Test
import org.litote.kmongo.getCollection

class LiveFootballTest {

    // db.channel.insertOne({channelName : "матч тв", lastLoadTime : 0, url : "matchtv" })

    @Test
    fun addChannel() {
        Storage.db.getCollection<Channel>().insertOne(Channel(null, 0, "matchtv", "матч тв"))
    }

    @Test
    fun addFootball() {
        Storage.db.getCollection<TVProgramItem>()
                .insertOne(TVProgramItem(
                        null,
                        System.currentTimeMillis() + 60000,
                        "Футбольчик",
                        "Матч тв",
                        "FOOTBALL"))
    }

    @Test
    fun getHtml() {
        val html = ZenitTicketService().loadZenitGames()
        println()
    }
}