package daocacao.myprecious.adapters.days

import android.graphics.Color.BLACK
import android.graphics.Color.GRAY
import android.graphics.Color.TRANSPARENT
import android.graphics.Typeface
import android.graphics.Typeface.DEFAULT
import android.graphics.Typeface.DEFAULT_BOLD
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import daocacao.myprecious.R
import daocacao.myprecious.adapters.days.DayType.ANOTHER_DAY
import daocacao.myprecious.adapters.days.DayType.CURRENT_MONTH
import daocacao.myprecious.adapters.days.DayType.TODAY
import daocacao.myprecious.adapters.days.SpendingType.BELOW_LIMIT
import daocacao.myprecious.adapters.days.SpendingType.NONE
import daocacao.myprecious.adapters.days.SpendingType.OVER_LIMIT
import daocacao.myprecious.data.Day
import daocacao.myprecious.inflate
import kotlinx.android.synthetic.main.item_day_of_month.view.txt_day
import kotlinx.android.synthetic.main.item_day_of_month.view.txt_money

class DayVH(parent: ViewGroup, private val onDayClick: (String) -> Unit) : RecyclerView.ViewHolder(parent.inflate(R.layout.item_day_of_month)) {

    private val goodDay by lazy { ContextCompat.getColor(itemView.context, R.color.goodDay) }
    private val goodDayTrans by lazy { ContextCompat.getColor(itemView.context, R.color.goodDayTrans) }
    private val badDay by lazy { ContextCompat.getColor(itemView.context, R.color.badDay) }
    private val badDayTrans by lazy { ContextCompat.getColor(itemView.context, R.color.badDayTrans) }
    private val dayAccent by lazy { ContextCompat.getColor(itemView.context, R.color.colorAccent) }

    fun bind(day: Day) {
        itemView.txt_day.text = day.date.split(".")[0]

        val totalMoney = day.records.map { it.spended }.sum()
        itemView.txt_money.text = if (totalMoney == 0f) "-" else totalMoney.toString()

        itemView.setOnClickListener { onDayClick.invoke(day.date) }
    }

    fun setType(dayType: DayType, spendingType: SpendingType) {
        when (dayType) {
            CURRENT_MONTH -> when (spendingType) {
                NONE -> setType(TRANSPARENT, BLACK, DEFAULT)
                BELOW_LIMIT -> setType(goodDay, BLACK, DEFAULT)
                OVER_LIMIT -> setType(badDay, BLACK, DEFAULT)
            }
            TODAY -> when (spendingType) {
                NONE -> setType(TRANSPARENT, dayAccent, DEFAULT_BOLD)
                BELOW_LIMIT -> setType(goodDay, dayAccent, DEFAULT_BOLD)
                OVER_LIMIT -> setType(badDay, dayAccent, DEFAULT_BOLD)
            }
            ANOTHER_DAY -> when (spendingType) {
                NONE -> setType(TRANSPARENT, GRAY, DEFAULT)
                BELOW_LIMIT -> setType(goodDayTrans, GRAY, DEFAULT)
                OVER_LIMIT -> setType(badDayTrans, GRAY, DEFAULT)
            }
        }
    }

    private fun setType(backgroundColor: Int, dayColor: Int, dayTypeface: Typeface) {
        itemView.setBackgroundColor(backgroundColor)
        itemView.txt_day.setTextColor(dayColor)
        itemView.txt_money.setTextColor(dayColor)
        itemView.txt_day.typeface = dayTypeface
        itemView.txt_money.typeface = dayTypeface
    }
}