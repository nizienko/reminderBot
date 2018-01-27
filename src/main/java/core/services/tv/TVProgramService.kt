package core.services.tv

import com.mongodb.client.FindIterable
import core.db.Storage
import core.utils.Scheduler.runEvery
import core.utils.toDateTime
import org.bson.types.ObjectId
import org.jsoup.Jsoup
import org.litote.kmongo.MongoOperator.*
import org.litote.kmongo.deleteMany
import org.litote.kmongo.find
import org.litote.kmongo.getCollection
import org.litote.kmongo.updateOneById
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class TVProgramService {
    init {
        runEvery(60000 * 60) {
            Storage.db.getCollection<Channel>().find()
                    .filter {
                        it.lastLoadTime.toDateTime().dayOfMonth != ZonedDateTime.now().dayOfMonth
                    }
                    .forEach { channel ->
                        println("Загружаем программу $channel на сегодня")
                        loadTvProgram(channel).forEach { item ->
                            Storage.db.getCollection<TVProgramItem>().insertOne(item)
                        }
                        channel.lastLoadTime = System.currentTimeMillis()
                        Storage.db.getCollection<Channel>().updateOneById(channel._id!!, channel)
                    }
            Storage.db.getCollection<TVProgramItem>().deleteMany("{time: {$lt: ${System.currentTimeMillis()}}}")
        }
    }

    fun getProgramsIn(nextTimeMillis: Long): FindIterable<TVProgramItem> =
        Storage.db.getCollection<TVProgramItem>()
                .find("{time: {$lt: ${System.currentTimeMillis() + nextTimeMillis}}}")

    private fun loadTvProgram(channel: Channel): List<TVProgramItem> {
        val items = mutableListOf<TVProgramItem>()
        val dtStr = ZonedDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        val html = Jsoup.connect("http://www.s-tv.ru/tv/${channel.url}/$dtStr").get()
        val prgItems = html.getElementsByAttributeValue("class", "prg_items").first()

        var day = ZonedDateTime.now()
        var hour = 0
        prgItems.getElementsByClass("prg_item")
                .forEach {
                    val time = it.getElementsByClass("prg_item_time").text()
                    val newHour = time.split(".")[0].toInt()
                    if (hour > newHour) {
                        day = day.plusDays(1)
                    }
                    hour = newHour
                    val minute = time.split(".")[1].toInt()
                    val dt = day.withHour(hour).withMinute(minute).withSecond(0)
                    try {
                        items.add(
                                TVProgramItem(
                                        null,
                                        dt.toInstant().toEpochMilli(),
                                        it.text().replace("LIVE", "").trim(),
                                        channel.channelName,
                                        TVProgramType.getType(it.text()).name)
                        )
                    } catch (e: Exception) {}
                }
        return items
    }
}

data class Channel(
        val _id: ObjectId?,
        var lastLoadTime: Long,
        val url: String,
        val channelName: String)

data class TVProgramItem(
        val _id: ObjectId?,
        val time: Long,
        val title: String,
        val channel: String,
        val type: String)



