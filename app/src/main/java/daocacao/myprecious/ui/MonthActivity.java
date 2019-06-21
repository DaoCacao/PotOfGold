package daocacao.myprecious.ui;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import daocacao.myprecious.AppLoader;
import daocacao.myprecious.adapters.CalendarRecyclerAdapter;
import daocacao.myprecious.R;
import daocacao.myprecious.data.Day;
import daocacao.myprecious.data.Gold;
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

        btnLeft.setOnClickListener(v -> updateCalendar(-1));
        btnRight.setOnClickListener(v -> updateCalendar(1));

        fab.setOnClickListener(v -> dayFragment.addNewGoldDialog());
        fab.hide();

        updateCalendar(0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        showEditLimitsDialog();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        showTotalInMonth();
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
        if (count != 0) AppLoader.updateCalendar(count);

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
        showTotalInMonth();
    }

    private void showTotalInMonth() {
        float total = 0;
        int currentMonth = AppLoader.currentDate[1];
        int currentYear = AppLoader.currentDate[2];
        RealmResults<Day> days = AppLoader.realm.where(Day.class)
                .equalTo("year", currentYear)
                .equalTo("month", currentMonth)
                .findAll();

        for (Day day : days)
            if (!day.getGolds().isEmpty())
                for (Gold gold : day.getGolds())
                    total += gold.getPrice();

        txtTotal.setText(String.format(Locale.getDefault(), "%s %.02f", "Spended:", total));
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

    private void showEditLimitsDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.add_gold_layout, new LinearLayout(this));
        EditText edDayTotal = (EditText) view.findViewById(R.id.ed_first);
        EditText edMonthTotal = (EditText) view.findViewById(R.id.ed_second);

        edDayTotal.setHint(R.string.txt_day_limit);
        edMonthTotal.setVisibility(View.GONE);

        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setView(view)
                .setPositiveButton("Ok", null)
                .create();

        alertDialog.setOnShowListener(dialog ->
                alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(v -> {

                    if (!TextUtils.isEmpty(edDayTotal.getText())) {
                        float dayLimit = Float.parseFloat(edDayTotal.getText().toString());
                        AppLoader.savePrefs(dayLimit);
                        dialog.dismiss();
                        adapter.notifyDataSetChanged();
                        showTotalInMonth();
                        if (dayFragment.isVisible()) dayFragment.setTotal();
                    } else
                        edDayTotal.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shake_txt_anim));
                }));

        alertDialog.show();
    }
}
