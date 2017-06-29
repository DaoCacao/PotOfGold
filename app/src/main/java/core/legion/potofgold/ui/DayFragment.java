package core.legion.potofgold.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Locale;

import core.legion.potofgold.AppLoader;
import core.legion.potofgold.R;
import core.legion.potofgold.models.Day;
import core.legion.potofgold.models.Gold;
import io.realm.RealmList;


public class DayFragment extends Fragment {

    ListView listView;
    TextView txtTotal;

    private RealmList<Gold> golds;
    private String date;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.day_layout, null);

        listView = (ListView) v.findViewById(R.id.list_view);
        txtTotal = (TextView) v.findViewById(R.id.txt_total);

        if (AppLoader.realm.where(Day.class).equalTo("date", date).count() == 0) {
            AppLoader.realm.executeTransaction(realm -> {
                AppLoader.realm.copyToRealmOrUpdate(new Day(date));
                golds = AppLoader.realm.where(Day.class).contains("date", date).findFirst().getGolds();
            });

        } else
            golds = AppLoader.realm.where(Day.class).contains("date", date).findFirst().getGolds();


        BaseAdapter adapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return golds.size();
            }

            @Override
            public Object getItem(int position) {
                return golds.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = convertView;
                if (v == null) v = View.inflate(parent.getContext(), R.layout.gold_item, null);

                TextView txtPrice = (TextView) v.findViewById(R.id.txt_price);
                TextView txtComment = (TextView) v.findViewById(R.id.txt_comment);

                txtPrice.setText(String.valueOf(golds.get(position).getPrice()));
                txtComment.setText(golds.get(position).getComment());

                return v;
            }
        };
        listView.setAdapter(adapter);
        setTotal();
        return v;
    }

    public void setTotal() {
        int total = 0;
        if (!golds.isEmpty()) for (Gold gold : golds) total += gold.getPrice();
        txtTotal.setText(String.format(Locale.getDefault(), "%d/%d", total, AppLoader.dayLimit));
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDate() {
        return date;
    }


}
