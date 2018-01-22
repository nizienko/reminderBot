package core.services.reminder

enum class RepeatType(val text: String) {
    NEVER(""),
    DAY("Каждый день"),
    WORK_DAYS("По будням"),
    WEEKENDS("По выходным"),
    WEEK("Раз в неделю"),
    WEEK2("Раз в две недели"),
    MONTH("Каждый месяц"),
    YEAR("Каждый год")
}