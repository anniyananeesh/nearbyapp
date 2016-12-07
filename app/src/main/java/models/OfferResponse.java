package models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Macbook on 06/10/16.
 */
public class OfferResponse {

    @SerializedName("data")
    private List<Offers> data;
    @SerializedName("code")
    private int responseCode;

    public List<Offers> getData() {
        return data;
    }

    public void setData(List<Offers> data) {
        this.data = data;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }
}
