package core.legion.potofgold.adapters;

import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.Gravity;
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

    private final int TODAY = 0;
    private final int CURRENT_MONTH = 1;
    private final int ANOTHER_DAY = 2;

    private final MonthActivity activity;
    private List<Date> cells;

    private final int goodDay, goodDayTrans, badDay, badDayTrans, dayAccent;
    private int currentDay, currentMonth, currentYear;

    private int backgroundColor;
    private int dayColor;
    private Typeface dayTypeface;

    private float goldTotal;

    public CalendarRecyclerAdapter(MonthActivity activity) {
        this.activity = activity;
        cells = new ArrayList<>();

        currentDay = AppLoader.currentDate[0];
        currentMonth = AppLoader.currentDate[1];
        currentYear = AppLoader.currentDate[2];

        goodDay = ContextCompat.getColor(activity, R.color.goodDay);
        goodDayTrans = ContextCompat.getColor(activity, R.color.goodDayTrans);

        badDay = ContextCompat.getColor(activity, R.color.badDay);
        badDayTrans = ContextCompat.getColor(activity, R.color.badDayTrans);

        dayAccent = ContextCompat.getColor(activity, R.color.colorAccent);
    }

    class VH extends RecyclerView.ViewHolder {

        final FrameLayout rootLayout;
        final TextView txtDay, txtMoney;

        VH(View itemView) {
            super(itemView);
            rootLayout = (FrameLayout) itemView;
            txtDay = (TextView) itemView.findViewById(R.id.txt_day);
            txtMoney = (TextView) itemView.findViewById(R.id.txt_money);

            rootLayout.setOnClickListener(v -> activity.showDay(getDate(getAdapterPosition())));
        }
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        FrameLayout rootLayout = new FrameLayout(parent.getContext());
        rootLayout.setLayoutParams(new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER));
        return new VH(LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.cell_item, rootLayout));
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {

        prepareDayColors(position);

        holder.rootLayout.setBackgroundColor(backgroundColor);

        holder.txtDay.setText(DateFormat.format("dd", cells.get(position)));
        holder.txtDay.setTextColor(dayColor);
        holder.txtDay.setTypeface(dayTypeface);

        holder.txtMoney.setText((goldTotal != 0) ? String.valueOf(goldTotal) : "-");
        holder.txtMoney.setTextColor(dayColor);
        holder.txtMoney.setTypeface(dayTypeface);
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

    private void prepareDayColors(int pos) {
        int cellDay = Integer.parseInt((String) DateFormat.format("d", cells.get(pos)));
        int cellMonth = Integer.parseInt((String) DateFormat.format("M", cells.get(pos)));
        int cellYear = Integer.parseInt((String) DateFormat.format("yyyy", cells.get(pos)));

        RealmQuery<Day> query = AppLoader.realm.where(Day.class).equalTo("date", getDate(pos));
        goldTotal = (query.count() != 0) ? getTotal(query.findFirst()) : 0;

        int day;
        if (cellYear == currentYear && cellMonth == currentMonth)
            day = (cellDay == currentDay) ?
                    TODAY :
                    CURRENT_MONTH;
        else day = ANOTHER_DAY;

        if (goldTotal != 0)
            backgroundColor = (goldTotal > AppLoader.DAY_LIMIT) ?
                    (day == TODAY || day == CURRENT_MONTH) ? badDay : badDayTrans :
                    (day == TODAY || day == CURRENT_MONTH) ? goodDay : goodDayTrans;
        else backgroundColor = Color.TRANSPARENT;

        if (day == TODAY) dayColor = dayAccent;
        else dayColor = (day == CURRENT_MONTH) ? Color.BLACK : Color.GRAY;

        dayTypeface = (day == TODAY) ? Typeface.DEFAULT_BOLD : Typeface.DEFAULT;
    }
}

