package core.legion.potofgold.data;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Day extends RealmObject {

    @PrimaryKey
    private String date;
    private int month;
    private RealmList<Gold> golds;

    public Day() {
    }
    public Day(String date) {
        this.date = date;
        month = Integer.parseInt(date.split("\\.")[1]);
        golds = new RealmList<>();
    }

    public RealmList<Gold> getGolds() {
        return golds;
    }
}
