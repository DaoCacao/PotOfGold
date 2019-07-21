package daocacao.myprecious

import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View.GONE
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import daocacao.myprecious.data.Record
import kotlinx.android.synthetic.main.dialog_add_record.view.ed_first
import kotlinx.android.synthetic.main.dialog_add_record.view.ed_second

class DialogManager(private val context: Context) {

    fun showEditLimitsDialog(onSuccess: (Float) -> Unit) {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_add_record, null)

        view.ed_first.setHint(R.string.txt_day_limit)
        view.ed_second.visibility = GONE

        val alertDialog = AlertDialog.Builder(context)
                .setView(view)
                .setPositiveButton("Ok", null)
                .create()

        alertDialog.setOnShowListener { dialog ->
            alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener { v ->

                if (view.ed_first.text.isNotBlank()) {
                    onSuccess.invoke(view.ed_first.text.toString().toFloat())
                    dialog.dismiss()
                } else
                    view.ed_first.startAnimation(AnimationUtils.loadAnimation(context, R.anim.shake_txt_anim))
            }
        }

        alertDialog.show()
    }

    fun showNewRecordDialog(onSuccess: (Pair<Float, String>) -> Unit) {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_add_record, LinearLayout(context))

        view.ed_first.setHint(R.string.txt_price)
        view.ed_second.setHint(R.string.txt_comment)

        val alertDialog = AlertDialog.Builder(context)
                .setView(view)
                .setPositiveButton("Ok", null)
                .create()

        alertDialog.setOnShowListener { dialog ->
            alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener { v ->
                if (view.ed_first.text.isNotBlank()) {
                    val price = view.ed_first.text.toString().toFloat()
                    val comment = view.ed_second.text.toString()

                    onSuccess.invoke(price to comment)
                    dialog.dismiss()
                } else
                    view.ed_first.startAnimation(AnimationUtils.loadAnimation(context, R.anim.shake_txt_anim))
            }
        }

        alertDialog.show()
    }

    fun showEditRecordDialog(record: Record, onEdit: (Pair<Float, String>) -> Unit, onDelete: () -> Unit) {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_add_record, null)
        view.ed_first.setText(record.spended.toString())
        view.ed_first.setHint(R.string.txt_price)

        view.ed_second.setText(record.comment)
        view.ed_second.setHint(R.string.txt_comment)

        val alertDialog = AlertDialog.Builder(context)
                .setView(view)
                .setPositiveButton("Ok", null)
                .setNeutralButton("Delete", null)
                .create()

        alertDialog.setOnShowListener { dialog ->
            alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener { v ->
                if (view.ed_first.text.isNotBlank()) {
                    val price = view.ed_first.text.toString().toFloat()
                    val comment = view.ed_second.text.toString()

                    onEdit.invoke(price to comment)

                    dialog.dismiss()
                } else
                    view.ed_first.startAnimation(AnimationUtils.loadAnimation(context, R.anim.shake_txt_anim))
            }
            alertDialog.getButton(DialogInterface.BUTTON_NEUTRAL).setOnClickListener { v ->
                onDelete.invoke()
                dialog.dismiss()
            }
        }

        alertDialog.show()
    }
}