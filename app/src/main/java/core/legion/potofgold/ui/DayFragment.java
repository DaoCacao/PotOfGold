package core.legion.potofgold.ui;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Locale;

import core.legion.potofgold.AppLoader;
import core.legion.potofgold.R;
import core.legion.potofgold.adapters.GoldRecyclerAdapter;
import core.legion.potofgold.data.Day;
import core.legion.potofgold.data.Gold;
import io.realm.RealmQuery;

public class DayFragment extends Fragment {

    private GoldRecyclerAdapter adapter;

    private TextView txtTotal;

    private Day day;
    private String date;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = LayoutInflater.from(getContext()).inflate(R.layout.day_layout, new RelativeLayout(getContext()));
        RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.recycler_view);
        txtTotal = (TextView) v.findViewById(R.id.txt_total);

        RealmQuery<Day> query = AppLoader.realm.where(Day.class).equalTo("date", date);
        day = (query.count() == 0)
                ? new Day(date)
                : query.findFirst();

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);

        adapter = new GoldRecyclerAdapter(this, day.getGolds());

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        setTotal();
        return v;
    }

    public void setTotal() {
        int total = 0;
        if (!day.getGolds().isEmpty()) for (Gold gold : day.getGolds()) total += gold.getPrice();
        txtTotal.setText(String.format(Locale.getDefault(), "%d/%d", total, AppLoader.dayLimit));
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void addNewGoldDialog() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.add_gold_layout, new LinearLayout(getContext()));
        EditText edPrice = (EditText) view.findViewById(R.id.ed_price);
        EditText edComment = (EditText) view.findViewById(R.id.ed_comment);

        AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                .setView(view)
                .setPositiveButton("Ok", null)
                .create();

        alertDialog.setOnShowListener(dialog ->
                alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(v -> {
                    if (!TextUtils.isEmpty(edPrice.getText()))
                        AppLoader.realm.executeTransaction(realm -> {
                            float price = Float.parseFloat(edPrice.getText().toString());
                            String comment = edComment.getText().toString();

                            day.getGolds().add(new Gold(price, comment));
                            AppLoader.realm.copyToRealmOrUpdate(day);
                            dialog.dismiss();

                            adapter.notifyDataSetChanged();
                            setTotal();
                        });
                    else
                        edPrice.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.shake_txt_anim));
                }));

        alertDialog.show();
    }

    public void editGoldDialog(Gold gold) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.add_gold_layout, new LinearLayout(getContext()));
        EditText edPrice = (EditText) view.findViewById(R.id.ed_price);
        EditText edComment = (EditText) view.findViewById(R.id.ed_comment);

        edPrice.setText(String.valueOf(gold.getPrice()));
        edComment.setText(gold.getComment());

        AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                .setView(view)
                .setPositiveButton("Ok", null)
                .setNeutralButton("Delete", null)
                .create();

        alertDialog.setOnShowListener(dialog -> {
            alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(v -> {
                if (!TextUtils.isEmpty(edPrice.getText()))
                    AppLoader.realm.executeTransaction(realm -> {

                        float price = Float.parseFloat(edPrice.getText().toString());
                        String comment = edComment.getText().toString();

                        gold.setPrice(price);
                        gold.setComment(comment);
                        AppLoader.realm.copyToRealmOrUpdate(day);
                        dialog.dismiss();

                        adapter.notifyDataSetChanged();
                        setTotal();
                    });
                else
                    edPrice.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.shake_txt_anim));
            });
        });


        alertDialog.show();
    }
}
