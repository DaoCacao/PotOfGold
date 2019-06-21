package daocacao.myprecious.data;

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

    public void setPrice(float price) {
        this.price = price;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }
}