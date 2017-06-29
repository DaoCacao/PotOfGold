package core.legion.potofgold;

import android.app.Application;

import java.util.Calendar;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class AppLoader extends Application {

    public static volatile Realm realm;

    public static Calendar currentCalendar;
    public static int[] currentDate;

    public static final int DAYS_COUNT = 42;

    public static final int monthLimit = 10000;
    public static final int dayLimit = 300;

    @Override
    public void onCreate() {
        super.onCreate();

        Realm.init(this);
        realm = Realm.getInstance(
                new RealmConfiguration.Builder()
                        .name("Pot_of_Gold")
                        .schemaVersion(0)
                        .deleteRealmIfMigrationNeeded()
                        .build());

        currentCalendar = Calendar.getInstance();

        currentDate = new int[]{
                currentCalendar.get(Calendar.DAY_OF_MONTH),
                currentCalendar.get(Calendar.MONTH) + 1,
                currentCalendar.get(Calendar.YEAR)};
    }
}
