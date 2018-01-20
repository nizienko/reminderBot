package core.services.reminder

enum class RepeatType(val text: String) {
    NEVER(""),
    DAY("каждый день"),
    WEEK("раз в неделю"),
    WEEK2("раз в две недели"),
    MONTH("каждый месяц"),
    YEAR("каждый год")
}