package core.utils

import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking

object Scheduler {
    fun runEvery(ms: Long, block: suspend () -> Unit) = runBlocking {
        launch {
            while (true) {
                block()
                delay(ms)
            }
        }
    }
}