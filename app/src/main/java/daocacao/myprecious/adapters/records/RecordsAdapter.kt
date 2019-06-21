package daocacao.myprecious.adapters.records

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import daocacao.myprecious.data.Record
import io.realm.RealmList

class RecordsAdapter : RecyclerView.Adapter<RecordVH>() {

    var onRecordClick: (Record) -> Unit = { }

    var records = RealmList<Record>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = RecordVH(parent, onRecordClick)

    override fun onBindViewHolder(holder: RecordVH, position: Int) = holder.bind(records[position])

    override fun getItemCount() = records.size
}

