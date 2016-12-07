package models;

import com.google.gson.annotations.SerializedName;


public class PlaceDetailsResponse {

    @SerializedName("result")
    private PlaceDetails result;

    @SerializedName("code")
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public PlaceDetails getResult() {
        return result;
    }

    public void setResult(PlaceDetails result) {
        this.result = result;
    }

}