package core.services.zenitTikets

import core.db.Storage
import core.services.zenitTikets.KindOfSport.BASKETBALL
import core.services.zenitTikets.KindOfSport.FOOLBALL
import core.utils.Scheduler
import org.bson.types.ObjectId
import org.jsoup.Jsoup
import org.litote.kmongo.deleteOneById
import org.litote.kmongo.getCollection
import org.litote.kmongo.updateOneById

class ZenitTicketService {
    init {
        Scheduler.runEvery(60000) {
            var isActualGame = false
            println("Обновляем данные по играм с сайта Зенита")
            val gamesFromSite = loadZenitGames()
            println("Актуализируем данные по играм в базе")
            //Delete from db old item and update actual data
            Storage.db.getCollection<Game>().find()
                    .forEach { game ->
                        gamesFromSite.forEach {
                            if (game == it) {
                                isActualGame = true
                                Storage.db.getCollection<Game>().updateOneById(game._id!!, it)
                            }
                        }
                        if (!isActualGame) {
                            Storage.db.getCollection<Game>().deleteOneById(game._id!!)
                        }
                    }
            println("Добавляем новые игры в базу")
            //Add to db new item
            val oldGames = Storage.db.getCollection<Game>().find()
            gamesFromSite.forEach { game ->
                oldGames.forEach {
                    if (game != it) {
                        Storage.db.getCollection<Game>().insertOne(game)
                        SubscriptionTicketService.sendNotifications(game)
                    }

                }
            }
        }
    }

    fun loadZenitGames(): MutableList<Game> {
        val games = mutableListOf<Game>()
        val html = Jsoup.connect("http://tickets.fc-zenit.ru/all-matches").get()
        val findGames = html.getElementsByAttributeValueContaining("class", "match expanding")

        findGames.forEach {
            val matchName = it.getElementsByClass("match__name").first().text().trim()
            val matchDate = it.getElementsByClass("match__date").first().text().trim()
            val freeTicket = it.getElementsByClass("match__count-val").first().text().toLong()
            val subname = it.getElementsByClass("match__subname").first().text().trim().split(".")
            var sport = "unknown"
            var league = "unknown"

            if (subname.size > 0) {
                when (subname[0]) {
                    BASKETBALL.type -> sport = BASKETBALL.type
                    FOOLBALL.type -> sport = FOOLBALL.type
                }

                if (subname.size > 1) {
                    league = subname[1].trim()
                }
            }

            games.add(
                    Game(null,
                            matchName,
                            matchDate,
                            freeTicket,
                            sport,
                            league
                    )
            )
        }

        return games
    }
}

data class Game(
        val _id: ObjectId?,
        val matchName: String,
        var matchDate: String,
        val freeTicket: Long,
        val sport: String,
        val league: String) {

    override fun equals(other: Any?): Boolean {

        if (this == other) {
            return true
        }
        if (other?.javaClass != javaClass) {
            return false
        }

        other as Game

        if (matchName.equals(other.matchName) &&
                matchDate.equals(other.matchDate) &&
                sport.equals(other.sport) &&
                league.equals(other.league))
            return true

        return false
    }

    override fun hashCode(): Int {
        val prime = 31
        var result = 1
        result = prime * result + matchName.hashCode()
        result = prime * result + matchDate.hashCode()
        result = prime * result + sport.hashCode()
        result = prime * result + league.hashCode()

        return result
    }

    fun toMessage(): String {
        return StringBuilder()
                .appendln("$sport:$league")
                .appendln(matchName)
                .appendln(matchDate)
                .toString()
    }
}
