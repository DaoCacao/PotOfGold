package core.legion.potofgold.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import core.legion.potofgold.AppLoader;
import core.legion.potofgold.CalendarAdapter;
import core.legion.potofgold.R;
import core.legion.potofgold.models.Day;
import core.legion.potofgold.models.Gold;
import io.realm.RealmResults;

public class MonthActivity extends AppCompatActivity {

    private final int DAYS_COUNT = 42;

    TextView txtMonthTitle;
    ImageView btnLeft, btnRight;
    GridView gridCalendar;
    TextView txtTotal;
    FloatingActionButton fab;

    CalendarAdapter calendarAdapter;

    DayFragment dayFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();

        dayFragment = new DayFragment();

        calendarAdapter = new CalendarAdapter();
        gridCalendar.setAdapter(calendarAdapter);

        btnLeft.setOnClickListener(v -> updateCalendar(-1));

        btnRight.setOnClickListener(v -> updateCalendar(1));

        gridCalendar.setOnItemClickListener((parent, view, position, id) -> showDay(position));

        txtTotal.setText(getTotal());

        fab.setOnClickListener(v -> addNewGoldDialog());
        fab.hide();

        updateCalendar(0);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        fab.hide();
    }

    private void initViews() {
        setContentView(R.layout.month_activity);

        txtMonthTitle = (TextView) findViewById(R.id.txt_month_title);
        btnLeft = (ImageView) findViewById(R.id.btn_left);
        btnRight = (ImageView) findViewById(R.id.btn_right);
        gridCalendar = (GridView) findViewById(R.id.grid_calendar);
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

        while (cells.size() < DAYS_COUNT) {
            cells.add(calendar.getTime());
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        calendarAdapter.updateData(cells);
    }

    private String getTotal() {
        int total = 0;
        String currentMonth = String.valueOf(AppLoader.currentCalendar.get(Calendar.MONTH) + 1);
        RealmResults<Day> days = AppLoader.realm.where(Day.class).contains("month", currentMonth).findAll();

        for (Day day : days)
            if (!day.getGolds().isEmpty())
                for (Gold gold : day.getGolds())
                    total += gold.getPrice();

        return String.format(Locale.getDefault(), "%d/%d", total, AppLoader.monthLimit);
    }

    private void showDay(int position) {
        dayFragment.setDate(calendarAdapter.getDate(position));
        getSupportFragmentManager()
                .beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .replace(R.id.container_layout, dayFragment)
                .addToBackStack("gold_frag")
                .commit();
        fab.show();
    }

    private void addNewGoldDialog() {
        View view = View.inflate(this, R.layout.add_gold_layout, null);
        EditText edPrice = (EditText) view.findViewById(R.id.ed_price);
        EditText edComment = (EditText) view.findViewById(R.id.ed_comment);

        new AlertDialog.Builder(this)
                .setView(view)
                .setPositiveButton("Ok", (dialog, which) -> {
                    AppLoader.realm.executeTransaction(realm ->
                            AppLoader.realm.where(Day.class).contains("date", dayFragment.getDate()).findFirst().getGolds().add(
                                    new Gold(Float.valueOf(
                                            edPrice.getText().toString()),
                                            edComment.getText().toString())));
                    getSupportFragmentManager().beginTransaction().replace(R.id.container_layout, dayFragment).commit();
                    dayFragment.setTotal();
                })
                .show();
    }
}
