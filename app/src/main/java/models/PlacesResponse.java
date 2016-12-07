package models;

import com.google.gson.annotations.SerializedName;

import java.util.List;


public class PlacesResponse {

    @SerializedName("results")
    private List<Places> results;

    @SerializedName("code")
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Places> getResults() {
        return results;
    }

    public void setResults(List<Places> results) {
        this.results = results;
    }

}