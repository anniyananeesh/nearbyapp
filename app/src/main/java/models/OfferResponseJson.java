package models;

import com.google.gson.annotations.SerializedName;

public class OfferResponseJson {

    @SerializedName("data")
    private Offer result;

    @SerializedName("code")
    private int status;

    public Offer getResult() {
        return result;
    }

    public void setResult(Offer result) {
        this.result = result;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}