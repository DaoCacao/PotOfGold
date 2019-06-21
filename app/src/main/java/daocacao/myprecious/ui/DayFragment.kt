package daocacao.myprecious.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import daocacao.myprecious.AppPrefs
import daocacao.myprecious.DialogManager
import daocacao.myprecious.R
import daocacao.myprecious.RealmManager
import daocacao.myprecious.adapters.records.RecordsAdapter
import daocacao.myprecious.data.Record
import kotlinx.android.synthetic.main.fragment_day.fab
import kotlinx.android.synthetic.main.fragment_day.recycler_view
import kotlinx.android.synthetic.main.fragment_day.txt_total
import java.util.*

class DayFragment : Fragment() {

    companion object {
        fun getInstance(date: String) = DayFragment().apply { this.date = date }
    }

    private lateinit var date: String

    private val prefs by lazy { AppPrefs(requireContext()) }
    private val realmManager by lazy { RealmManager(requireContext()) }

    private val adapter by lazy { RecordsAdapter() }
    private val day by lazy { realmManager.getDay(date) }

    private val dialogManager by lazy { DialogManager(requireContext()) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return LayoutInflater.from(context).inflate(R.layout.fragment_day, RelativeLayout(context))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recycler_view.adapter = adapter

        adapter.records = day.records
        adapter.onRecordClick = this::editRecord

        fab.setOnClickListener { addNewGoldDialog() }

        updateInfo()
    }

    private fun addNewGoldDialog() {
        dialogManager.showNewRecordDialog { priceComment ->
            realmManager.addRecord(day, priceComment.first, priceComment.second)
            updateInfo()
        }
    }

    private fun editRecord(record: Record) {
        dialogManager.showEditRecordDialog(record) { priceComment ->
            realmManager.editRecord(record) {
                it.spended = priceComment.first
                it.comment = priceComment.second
            }
            updateInfo()
        }
    }

    private fun updateInfo() {
        adapter.notifyDataSetChanged()

        val total = day.records.map { it.spended }.sum()
        txt_total.text = String.format(Locale.getDefault(), "%.02f/%.02f", total, prefs.dayLimit)
    }
}