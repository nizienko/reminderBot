package core

import java.io.File

fun loadSettings(): Map<String, String> {
    val result = mutableMapOf<String, String>()
    File("settings")
            .readLines().forEach {
        var str = it
        if (str.contains("#")) {
            str = str.split("#")[0]
        }
        if (str.contains("=")) {
            val key = str.substringBefore("=").trim()
            val value = str.substringAfter("=").trim()
            result.put(key, value)
        }
    }
    return result
}