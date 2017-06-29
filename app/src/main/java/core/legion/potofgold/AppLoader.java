package core.legion.potofgold;

import android.app.Application;

import java.util.Calendar;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmMigration;

public class AppLoader extends Application {

    public static volatile Realm realm;

    public static Calendar currentCalendar;
    public static int currentMonth;
    public static int currentDay;

    public static int monthLimit = 10000;
    public static int dayLimit = 300;

    @Override
    public void onCreate() {
        super.onCreate();

        Realm.init(this);
        realm = Realm.getInstance(
                new RealmConfiguration.Builder()
                        .name("Pot_of_Gold")
                        .schemaVersion(0)
                        .deleteRealmIfMigrationNeeded()
//                        .migration(initMigration())
                        .build());

        currentCalendar = Calendar.getInstance();
        currentMonth = currentCalendar.get(Calendar.MONTH) + 1;
        currentDay = currentCalendar.get(Calendar.DAY_OF_MONTH);
    }

    private RealmMigration initMigration() {
        return (realm1, oldVersion, newVersion) -> {
            if (oldVersion < newVersion) {
                oldVersion++;
            }
        };
    }

}
