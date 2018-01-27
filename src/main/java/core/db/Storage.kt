package core.db

import core.ctx.AppContext
import org.litote.kmongo.KMongo

object Storage {
    val db = KMongo.createClient(
            AppContext.settings["db.host"]!!,
            AppContext.settings["db.port"]!!.toInt()
    ).getDatabase("botdb")!!
}