package daocacao.myprecious;

import android.app.Application;
import android.content.SharedPreferences;

import java.util.Calendar;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class AppLoader extends Application {

    private static SharedPreferences prefs;

    public static volatile Realm realm;

    public static Calendar currentCalendar;
    public static int[] currentDate;

    public static final int DAYS_COUNT = 42;

    public static float DAY_LIMIT;

    @Override
    public void onCreate() {
        super.onCreate();

        prefs = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);
        loadPrefs();

        Realm.init(this);
        realm = Realm.getInstance(
                new RealmConfiguration.Builder()
                        .name(getString(R.string.app_name))
                        .schemaVersion(0)
                        .deleteRealmIfMigrationNeeded()
                        .build());

        currentCalendar = Calendar.getInstance();
        currentDate = new int[]{
                currentCalendar.get(Calendar.DAY_OF_MONTH),
                currentCalendar.get(Calendar.MONTH) + 1,
                currentCalendar.get(Calendar.YEAR)};
    }

    private void loadPrefs() {
        DAY_LIMIT = prefs.getFloat("DAY_LIMIT", 300);
    }

    public static void savePrefs(float newDayLimit) {
        DAY_LIMIT = newDayLimit;
        prefs.edit()
                .putFloat("DAY_LIMIT", DAY_LIMIT)
                .apply();
    }

    public static void updateCalendar(int count) {
        currentCalendar.add(Calendar.MONTH, count);
        currentDate = new int[]{
                currentCalendar.get(Calendar.DAY_OF_MONTH),
                currentCalendar.get(Calendar.MONTH) + 1,
                currentCalendar.get(Calendar.YEAR)};
    }
}
