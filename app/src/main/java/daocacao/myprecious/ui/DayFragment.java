package daocacao.myprecious.ui;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import daocacao.myprecious.AppLoader;
import daocacao.myprecious.R;
import daocacao.myprecious.adapters.GoldRecyclerAdapter;
import daocacao.myprecious.data.Day;
import daocacao.myprecious.data.Gold;
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
        txtTotal.setText(String.format(Locale.getDefault(), "%d/%.02f", total, AppLoader.DAY_LIMIT));
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void addNewGoldDialog() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.add_gold_layout, new LinearLayout(getContext()));

        EditText edPrice = (EditText) view.findViewById(R.id.ed_first);
        EditText edComment = (EditText) view.findViewById(R.id.ed_second);

        edPrice.setHint(R.string.txt_price);
        edComment.setHint(R.string.txt_comment);

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
        EditText edPrice = (EditText) view.findViewById(R.id.ed_first);
        EditText edComment = (EditText) view.findViewById(R.id.ed_second);

        edPrice.setText(String.valueOf(gold.getPrice()));
        edPrice.setHint(R.string.txt_price);

        edComment.setText(gold.getComment());
        edComment.setHint(R.string.txt_comment);

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
            alertDialog.getButton(DialogInterface.BUTTON_NEUTRAL).setOnClickListener(v -> {
                AppLoader.realm.executeTransaction(realm -> gold.deleteFromRealm());
                dialog.dismiss();

                adapter.notifyDataSetChanged();
                setTotal();
            });
        });

        alertDialog.show();
    }
}