package core.legion.potofgold;

import android.graphics.Color;
import android.text.format.DateFormat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import core.legion.potofgold.models.Day;
import core.legion.potofgold.models.Gold;
import io.realm.RealmResults;

public class CalendarAdapter extends BaseAdapter {

    private List<Date> cells;
    private SimpleDateFormat sdf;

    public CalendarAdapter() {
        cells = new ArrayList<>();
        sdf = new SimpleDateFormat("dd", Locale.getDefault());
    }

    @Override
    public int getCount() {
        return cells.size();
    }

    @Override
    public Object getItem(int position) {
        return cells.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int cellMonth = Integer.parseInt((String) DateFormat.format("M", cells.get(position)));
        int cellDay = Integer.parseInt((String) DateFormat.format("d", cells.get(position)));
        int cellYear = Integer.parseInt((String) DateFormat.format("y", cells.get(position)));

        View v = convertView;

        if (v == null) v = View.inflate(parent.getContext(), R.layout.cell_item, null);

        FrameLayout rootLayout = (FrameLayout) v.findViewById(R.id.root_layout);
        TextView txtDay = (TextView) v.findViewById(R.id.txt_day);

        if (getTotal(AppLoader.realm.where(Day.class).))

        txtDay.setText(sdf.format(cells.get(position)));

        if (cellMonth == AppLoader.currentMonth)
            txtDay.setTextColor((cellDay == AppLoader.currentDay) ? Color.BLUE : Color.BLACK);
        else txtDay.setTextColor(Color.GRAY);

        return v;
    }

    public void updateData(List<Date> cells) {
        this.cells = cells;
        notifyDataSetChanged();
    }

    public String getDate(int position) {
        return new SimpleDateFormat("d.M.y",  Locale.getDefault()).format(cells.get(position).getTime());
    }

    private String  getTotal(RealmResults<Gold> golds) {
        int total = 0;
        if (!golds.isEmpty()) for (Gold gold : golds) total += gold.getPrice();
        return String.format(Locale.getDefault(), "%d/%d", total, AppLoader.dayLimit);
    }
}

