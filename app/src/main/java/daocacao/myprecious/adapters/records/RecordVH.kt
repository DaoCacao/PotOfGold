package daocacao.myprecious.adapters.records

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import daocacao.myprecious.R
import daocacao.myprecious.data.Record
import daocacao.myprecious.inflate
import kotlinx.android.synthetic.main.item_record.view.txt_comment
import kotlinx.android.synthetic.main.item_record.view.txt_price

class RecordVH(parent: ViewGroup, private val onRecordClick: (Record) -> Unit) : RecyclerView.ViewHolder(parent.inflate(R.layout.item_record)) {

    fun bind(record: Record) {
        itemView.txt_price.text = record.spended.toString()
        itemView.txt_comment.text = record.comment

        itemView.setOnClickListener { onRecordClick.invoke(record) }
    }
}