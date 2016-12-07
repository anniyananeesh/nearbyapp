package models;

import com.google.gson.annotations.SerializedName;

public class UserResponse {

    @SerializedName("data")
    private User result;

    @SerializedName("code")
    private int status;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public User getResult() {
        return result;
    }

    public void setResult(User result) {
        this.result = result;
    }
}