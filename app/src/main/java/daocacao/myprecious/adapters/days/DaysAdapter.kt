package daocacao.myprecious.adapters.days

import android.content.Context
import android.text.format.DateFormat
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import daocacao.myprecious.AppPrefs
import daocacao.myprecious.CalendarManager
import daocacao.myprecious.RealmManager
import daocacao.myprecious.adapters.days.DayType.ANOTHER_DAY
import daocacao.myprecious.adapters.days.DayType.CURRENT_MONTH
import daocacao.myprecious.adapters.days.DayType.TODAY
import daocacao.myprecious.adapters.days.SpendingType.BELOW_LIMIT
import daocacao.myprecious.adapters.days.SpendingType.NONE
import daocacao.myprecious.adapters.days.SpendingType.OVER_LIMIT
import daocacao.myprecious.data.Day
import java.util.*

class DaysAdapter(context: Context) : RecyclerView.Adapter<DayVH>() {

    lateinit var calendarManager: CalendarManager

    var dayLimit = 0f
    var onDayClick: (String) -> Unit = {}

    var cells = emptyList<Date>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    private val realmManager by lazy { RealmManager(context) }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = DayVH(parent, onDayClick)

    override fun onBindViewHolder(holder: DayVH, position: Int) {
        val day = realmManager.getDay(DateFormat.format("d.M.yyyy", cells[position].time).toString())
        holder.bind(day)
        holder.setType(getDayType(day), getSpendingType(day))
    }

    override fun getItemCount() = cells.size

    private fun getDayType(day: Day): DayType {
        return when {
            calendarManager.isToday(day) -> TODAY
            calendarManager.isThisMonth(day) -> CURRENT_MONTH
            else -> ANOTHER_DAY
        }
    }

    private fun getSpendingType(day: Day): SpendingType {
        val spended = day.records.map { it.spended }.sum()
        return when {
            spended == 0f -> NONE
            spended <= dayLimit -> BELOW_LIMIT
            spended > dayLimit -> OVER_LIMIT
            else -> NONE
        }
    }
}

