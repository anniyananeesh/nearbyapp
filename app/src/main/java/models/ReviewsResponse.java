package models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ReviewsResponse {

    @SerializedName("data")
    private List<Reviews> result;

    @SerializedName("code")
    private int status;

    public List<Reviews> getResult() {
        return result;
    }

    public void setResult(List<Reviews> result) {
        this.result = result;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}