package core.legion.potofgold.models;

import io.realm.RealmObject;

public class Gold extends RealmObject {

    private float price;
    private String comment;

    public Gold() {
    }

    public Gold(float price, String comment) {
        this.price = price;
        this.comment = comment;
    }

    public float getPrice() {
        return price;
    }

    public String getComment() {
        return comment;
    }
}
