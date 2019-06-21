package daocacao.myprecious

import daocacao.myprecious.data.Day
import java.text.SimpleDateFormat
import java.util.*

class CalendarManager {

    val currentCalendar: Calendar by lazy { Calendar.getInstance() }
    var selectedDate = intArrayOf(
            currentCalendar.get(Calendar.DAY_OF_MONTH),
            currentCalendar.get(Calendar.MONTH) + 1,
            currentCalendar.get(Calendar.YEAR))

    fun getCurrentMonth(onSuccess: (String, ArrayList<Date>) -> Unit) = getMonth(0, onSuccess)
    fun getNextMonth(onSuccess: (String, ArrayList<Date>) -> Unit) = getMonth(1, onSuccess)
    fun getPrevMonth(onSuccess: (String, ArrayList<Date>) -> Unit) = getMonth(-1, onSuccess)

    private fun getMonth(step: Int, onSuccess: (String, ArrayList<Date>) -> Unit) {
        currentCalendar.add(Calendar.MONTH, step)
        selectedDate = intArrayOf(
                currentCalendar.get(Calendar.DAY_OF_MONTH),
                currentCalendar.get(Calendar.MONTH) + 1,
                currentCalendar.get(Calendar.YEAR))

        val days = ArrayList<Date>()
        val calendar = currentCalendar.clone() as Calendar
        calendar.set(Calendar.DAY_OF_MONTH, 1)

        val monthBeginningCell = calendar.get(Calendar.DAY_OF_WEEK) - 1

        calendar.add(Calendar.DAY_OF_MONTH, -monthBeginningCell)

        while (days.size < 42) { //count of days to show on screen
            days.add(calendar.time)
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        onSuccess.invoke(SimpleDateFormat("MMM yyyy", Locale.getDefault()).format(calendar.time), days)
    }

    fun isToday(day: Day): Boolean = day.year == selectedDate[2] && day.month == selectedDate[1] && day.date.split(".")[0].toInt() == selectedDate[0]
    fun isThisMonth(day: Day): Boolean = day.year == selectedDate[2] && day.month == selectedDate[1]
}