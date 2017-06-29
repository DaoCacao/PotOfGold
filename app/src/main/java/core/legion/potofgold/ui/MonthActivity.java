package core.legion.potofgold.ui;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import core.legion.potofgold.AppLoader;
import core.legion.potofgold.adapters.CalendarRecyclerAdapter;
import core.legion.potofgold.R;
import core.legion.potofgold.data.Day;
import core.legion.potofgold.data.Gold;
import io.realm.RealmResults;

public class MonthActivity extends AppCompatActivity {

    TextView txtMonthTitle;
    ImageView btnLeft, btnRight;
    RecyclerView recyclerView;
    TextView txtTotal;
    FloatingActionButton fab;

    CalendarRecyclerAdapter adapter;

    DayFragment dayFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();

        dayFragment = new DayFragment();

        adapter = new CalendarRecyclerAdapter(this);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 7));

        txtTotal.setText(getTotalInMonth());

        btnLeft.setOnClickListener(v -> updateCalendar(-1));
        btnRight.setOnClickListener(v -> updateCalendar(1));

        fab.setOnClickListener(v -> dayFragment.addNewGoldDialog());
        fab.hide();

        updateCalendar(0);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        txtTotal.setText(getTotalInMonth());
        adapter.notifyDataSetChanged();
        fab.hide();
    }

    private void initViews() {
        setContentView(R.layout.month_activity);

        txtMonthTitle = (TextView) findViewById(R.id.txt_month_title);
        btnLeft = (ImageView) findViewById(R.id.btn_left);
        btnRight = (ImageView) findViewById(R.id.btn_right);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        txtTotal = (TextView) findViewById(R.id.txt_total);
        fab = (FloatingActionButton) findViewById(R.id.fab);
    }

    private void updateCalendar(int count) {
        AppLoader.currentCalendar.add(Calendar.MONTH, count);

        List<Date> cells = new ArrayList<>();
        Calendar calendar = (Calendar) AppLoader.currentCalendar.clone();
        calendar.set(Calendar.DAY_OF_MONTH, 1);

        int monthBeginningCell = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        txtMonthTitle.setText(new SimpleDateFormat("MMM yyyy", Locale.getDefault()).format(calendar.getTime()));

        calendar.add(Calendar.DAY_OF_MONTH, -monthBeginningCell);

        while (cells.size() < AppLoader.DAYS_COUNT) {
            cells.add(calendar.getTime());
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        adapter.updateData(cells);
    }

    private String getTotalInMonth() {
        int total = 0;
        int currentMonth = AppLoader.currentDate[1];
        RealmResults<Day> days = AppLoader.realm.where(Day.class).equalTo("month", currentMonth).findAll();

        for (Day day : days)
            if (!day.getGolds().isEmpty())
                for (Gold gold : day.getGolds())
                    total += gold.getPrice();

        return String.format(Locale.getDefault(), "%d/%d", total, AppLoader.monthLimit);
    }

    public void showDay(String date) {
        dayFragment.setDate(date);
        getSupportFragmentManager()
                .beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .replace(R.id.container_layout, dayFragment)
                .addToBackStack("gold_frag")
                .commit();
        fab.show();
    }
}
