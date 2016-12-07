package models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Macbook on 04/12/16.
 */
public class NotificationsCategory {

    @SerializedName("type")
    private String type;

    @SerializedName("count")
    private int count;

    @SerializedName("last_offer")
    private String lastOffer;

    public String getLastOffer() {
        return lastOffer;
    }

    public void setLastOffer(String lastOffer) {
        this.lastOffer = lastOffer;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
