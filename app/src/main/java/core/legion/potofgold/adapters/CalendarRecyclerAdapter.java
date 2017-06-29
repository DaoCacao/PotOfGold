package core.legion.potofgold.adapters;

import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import core.legion.potofgold.AppLoader;
import core.legion.potofgold.R;
import core.legion.potofgold.data.Day;
import core.legion.potofgold.data.Gold;
import core.legion.potofgold.ui.MonthActivity;
import io.realm.RealmList;
import io.realm.RealmQuery;

public class CalendarRecyclerAdapter extends RecyclerView.Adapter<CalendarRecyclerAdapter.VH> {

    private final MonthActivity activity;
    private List<Date> cells;

    public CalendarRecyclerAdapter(MonthActivity activity) {
        this.activity = activity;
        cells = new ArrayList<>();
    }

    class VH extends RecyclerView.ViewHolder {

        final FrameLayout rootLayout;
        final TextView txtDay;

        VH(View itemView) {
            super(itemView);
            rootLayout = (FrameLayout) itemView;
            txtDay = (TextView) itemView.findViewById(R.id.txt_day);

            rootLayout.setOnClickListener(v -> activity.showDay(getDate(getAdapterPosition())));
        }
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        return new VH(LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.cell_item, new FrameLayout(parent.getContext())));
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
        int[] cellDate = new int[]{
                Integer.parseInt((String) DateFormat.format("d", cells.get(position))),
                Integer.parseInt((String) DateFormat.format("M", cells.get(position))),
                Integer.parseInt((String) DateFormat.format("yyyy", cells.get(position)))
        };

        RealmQuery<Day> query = AppLoader.realm.where(Day.class).equalTo("date", getDate(position));
        int backgroundColor = ((query.count() == 0)
                ? Color.TRANSPARENT
                : (getTotal(query.findFirst()) > AppLoader.dayLimit)
                ? Color.RED
                : Color.GREEN);

        int dayColor;
        Typeface dayTypeface;

        if (cellDate[2] == AppLoader.currentDate[2] && cellDate[1] == AppLoader.currentDate[1]) {
            if (cellDate[0] == AppLoader.currentDate[0]) {
                dayColor = Color.BLUE;
                dayTypeface = Typeface.DEFAULT_BOLD;
            } else {
                dayColor = Color.BLACK;
                dayTypeface = Typeface.DEFAULT;
            }
        } else {
            dayColor = Color.GRAY;
            dayTypeface = Typeface.DEFAULT;
        }

        holder.rootLayout.setBackgroundColor(backgroundColor);

        holder.txtDay.setText(DateFormat.format("dd", cells.get(position)));
        holder.txtDay.setTextColor(dayColor);
        holder.txtDay.setTypeface(dayTypeface);
    }

    @Override
    public int getItemCount() {
        return cells.size();
    }

    public void updateData(List<Date> cells) {
        this.cells = cells;
        notifyDataSetChanged();
    }

    private String getDate(int position) {
        return (String) DateFormat.format("d.M.yyyy", cells.get(position).getTime());
    }

    private float getTotal(Day day) {
        RealmList<Gold> golds = day.getGolds();
        int total = 0;
        if (!golds.isEmpty()) for (Gold gold : golds) total += gold.getPrice();
        return total;
    }
}

