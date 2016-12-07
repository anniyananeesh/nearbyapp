package models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Macbook on 04/12/16.
 */
public class NotificationsCategoryResponse {

    @SerializedName("data")
    private List<NotificationsCategory> results;

    @SerializedName("code")
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<NotificationsCategory> getResults() {
        return results;
    }

    public void setResults(List<NotificationsCategory> results) {
        this.results = results;
    }
}
