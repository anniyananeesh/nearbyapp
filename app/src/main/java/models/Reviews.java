package models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Macbook on 16/11/16.
 */
public class Reviews {

    @SerializedName("_id")
    private String id;

    @SerializedName("review_name")
    private String name;

    @SerializedName("review_email")
    private String email;

    @SerializedName("review_image")
    private String profileImage;

    @SerializedName("review_message")
    private String message;

    @SerializedName("review_rate")
    private int rate;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }
}
