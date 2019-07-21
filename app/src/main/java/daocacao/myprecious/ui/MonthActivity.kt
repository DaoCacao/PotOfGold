package daocacao.myprecious.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import daocacao.myprecious.AppPrefs
import daocacao.myprecious.CalendarManager
import daocacao.myprecious.DialogManager
import daocacao.myprecious.R
import daocacao.myprecious.adapters.days.DaysAdapter
import daocacao.myprecious.realm.RealmManager
import kotlinx.android.synthetic.main.activity_month.btn_left
import kotlinx.android.synthetic.main.activity_month.btn_right
import kotlinx.android.synthetic.main.activity_month.recycler_view
import kotlinx.android.synthetic.main.activity_month.txt_month_title
import kotlinx.android.synthetic.main.activity_month.txt_total
import java.util.*

class MonthActivity : AppCompatActivity() {

    private val adapter by lazy { DaysAdapter(this) }

    private val prefs by lazy { AppPrefs(this) }
    private val realmManager by lazy { RealmManager(this) }
    private val calendarManager by lazy { CalendarManager() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_month)

        recycler_view.adapter = adapter

        adapter.calendarManager = calendarManager
        adapter.dayLimit = prefs.dayLimit
        adapter.onDayClick = this::showDay

        btn_left.setOnClickListener { calendarManager.getPrevMonth(this::updateView) }
        btn_right.setOnClickListener { calendarManager.getNextMonth(this::updateView) }

        calendarManager.getCurrentMonth(this::updateView)

        realmManager.observable
                .subscribe {
                    adapter.update(it)
                    showTotalInMonth()
                }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        DialogManager(this).showEditLimitsDialog {
            prefs.dayLimit = it
            adapter.dayLimit = it
            adapter.notifyDataSetChanged()
            showTotalInMonth()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun updateView(title: String, days: ArrayList<Date>) {
        adapter.cells = days
        txt_month_title.text = title
        showTotalInMonth()
    }

    private fun showTotalInMonth() {
        val currentMonth = calendarManager.selectedDate[1]
        val currentYear = calendarManager.selectedDate[2]

        val month = realmManager.getMonth(currentMonth, currentYear)

        val total = month.map { it.records.map { it.spended }.sum() }.sum()
        val average = month.map { it.records.map { it.spended }.average() }.average()

        txt_total.text = String.format(Locale.getDefault(), "Spended: %.02f (%.02f)", total, average)
    }

    private fun showDay(date: String) {
        DayFragment.getInstance(date).apply {
            show(supportFragmentManager, this.javaClass.canonicalName)
        }
    }
}