package core.services.tv

enum class TVProgramType(val message: String, val supportedFun: (text: String) -> Boolean) {
    FOOTBALL(
            message = "о трансляциях футбола",
            supportedFun = { it.contains("LIVE") && it.toLowerCase().contains("футбол") }
    ),
    F1(
            message = "о трансляциях формулы 1",
            supportedFun = { it.contains("LIVE") && it.toLowerCase().contains("формула-1") }
    ),
    HOCKEY(
            message = "о трансляциях хоккея",
            supportedFun = {it.contains("LIVE") && it.toLowerCase().contains("хоккей")}
    ),
    BASKETBALL(
            message = "о трансляциях баскетбола",
            supportedFun = {it.contains("LIVE") && it.toLowerCase().contains("баскетбол")}
    ),
    FIGHT(
            message = "о трансляциях единоборств",
            supportedFun = {it.contains("LIVE") &&
                    (it.toLowerCase().contains("бокс")
                            || it.toLowerCase().contains("m-1")
                            || it.toLowerCase().contains("ufc")
                            || it.toLowerCase().contains("единобоств"))}
    ),
    RACING(
            message = "о трансляциях автогонок",
            supportedFun = {it.contains("LIVE") && it.toLowerCase().contains("автогонк")}
    );

    fun supported(text: String): Boolean = supportedFun(text)

    companion object {
        fun getType(text: String) = TVProgramType.values().first { it.supported(text) }
    }
}