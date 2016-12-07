package models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Macbook on 14/10/16.
 */
public class User {

    @SerializedName("subsc_name")
    private String name;

    @SerializedName("_id")
    private String id;

    @SerializedName("category")
    private String category;

    @SerializedName("subsc_email")
    private String emailAddress;

    @SerializedName("subsc_contact")
    private String contactNo;

    @SerializedName("subsc_address")
    private String address;

    @SerializedName("subsc_logo")
    private String logo;

    @SerializedName("subsc_cords")
    private Double[] cordinates;

    @SerializedName("offers")
    private String[] offers;

    @SerializedName("subsc_rating")
    private float rating;

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String[] getOffers() {
        return offers;
    }

    public void setOffers(String[] offers) {
        this.offers = offers;
    }

    public Double[] getCordinates() {
        return cordinates;
    }

    public void setCordinates(Double[] cordinates) {
        this.cordinates = cordinates;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getContactNo() {
        return contactNo;
    }

    public void setContactNo(String contactNo) {
        this.contactNo = contactNo;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
