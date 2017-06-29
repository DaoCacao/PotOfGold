package core.legion.potofgold.models;

import java.util.Locale;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Day extends RealmObject {

    @PrimaryKey
    private String date;
    private String day;
    private String month;
    private String year;
    private RealmList<Gold> golds;

    public Day() {
    }

    public Day(String date) {
        this.date = date;
        String[] mass = date.split("\\.");
        day = mass[0];
        month = mass[1];
        year = mass[2];
        golds = new RealmList<>();
    }

    public String getDate() {
        return date;
    }

    public String getDay() {
        return day;
    }

    public String getMonth() {
        return month;
    }

    public String getYear() {
        return year;
    }

    public RealmList<Gold> getGolds() {
        return golds;
    }
}
