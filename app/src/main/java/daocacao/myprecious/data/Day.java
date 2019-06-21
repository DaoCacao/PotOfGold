package daocacao.myprecious.data;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Day extends RealmObject {

    @PrimaryKey
    private String date;
    private int month;
    private int year;
    private RealmList<Gold> golds;

    public Day() {
    }

    public Day(String date) {
        this.date = date;
        month = Integer.parseInt(date.split("\\.")[1]);
        year = Integer.parseInt(date.split("\\.")[2]);
        golds = new RealmList<>();
    }

    public RealmList<Gold> getGolds() {
        return golds;
    }
}
