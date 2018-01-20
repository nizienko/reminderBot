package db

import core.db.Storage
import org.junit.jupiter.api.Test
import org.litote.kmongo.*

class MongoTest {

    @Test
    fun getTestCollection() {
        val o = Storage.db.getCollection<User>("user").findOne()
        println(o)
    }

    @Test
    fun testKMongo() {
        val collection = Storage.db.getCollection<User>()
        collection.insertOne(User("Ivan", 30))
    }
}