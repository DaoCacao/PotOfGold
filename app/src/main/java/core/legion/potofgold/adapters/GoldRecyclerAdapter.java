package core.legion.potofgold.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import core.legion.potofgold.R;
import core.legion.potofgold.data.Gold;
import core.legion.potofgold.ui.DayFragment;
import io.realm.RealmList;

public class GoldRecyclerAdapter extends RecyclerView.Adapter<GoldRecyclerAdapter.VH> {

    private final DayFragment dayFragment;
    private final RealmList<Gold> golds;

    public GoldRecyclerAdapter(DayFragment dayFragment, RealmList<Gold> golds) {
        this.dayFragment = dayFragment;
        this.golds = golds;
    }

    class VH extends RecyclerView.ViewHolder {

        final LinearLayout rootLayout;
        final TextView txtPrice, txtComment;

        VH(View itemView) {
            super(itemView);

            rootLayout = (LinearLayout) itemView.findViewById(R.id.root_layout);
            txtPrice = (TextView) itemView.findViewById(R.id.txt_price);
            txtComment = (TextView) itemView.findViewById(R.id.txt_comment);

            rootLayout.setOnClickListener(v -> dayFragment.editGoldDialog(golds.get(getAdapterPosition())));
        }
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        FrameLayout frameLayout = new FrameLayout(parent.getContext());
        frameLayout.setLayoutParams(new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));

        return new VH(LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.gold_item, frameLayout));
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
        holder.txtPrice.setText(String.valueOf(golds.get(position).getPrice()));
        holder.txtComment.setText(golds.get(position).getComment());
    }

    @Override
    public int getItemCount() {
        return golds.size();
    }
}