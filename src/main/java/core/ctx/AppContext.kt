package core.ctx

import core.Bot
import core.loadSettings
import core.services.tv.TVProgramService
import core.services.reminder.ReminderService
import core.services.tv.SubscriptionService


object AppContext {
    val bot = Bot()
    val settings = loadSettings()
    val tvProgram = TVProgramService()
    val reminderService = ReminderService()
    val subscriptionService = SubscriptionService()
}