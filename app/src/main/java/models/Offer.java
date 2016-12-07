package models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Macbook on 19/11/16.
 */
public class Offer {

    @SerializedName("_id")
    private String id;

    @SerializedName("subscriber_id")
    private String subscriber;

    @SerializedName("offer_title")
    private String offerTitle;

    @SerializedName("offer_image")
    private String offerImage;

    @SerializedName("offer_details")
    private String offerDescription;

    @SerializedName("offer_avldate")
    private String offerAvailDate;

    @SerializedName("offer_email")
    private String offerEmail;

    public String getOfferEmail() {
        return offerEmail;
    }

    public void setOfferEmail(String offerEmail) {
        this.offerEmail = offerEmail;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSubscriber() {
        return subscriber;
    }

    public void setSubscriber(String subscriber) {
        this.subscriber = subscriber;
    }

    public String getOfferTitle() {
        return offerTitle;
    }

    public void setOfferTitle(String offerTitle) {
        this.offerTitle = offerTitle;
    }

    public String getOfferImage() {
        return offerImage;
    }

    public void setOfferImage(String offerImage) {
        this.offerImage = offerImage;
    }

    public String getOfferDescription() {
        return offerDescription;
    }

    public void setOfferDescription(String offerDescription) {
        this.offerDescription = offerDescription;
    }

    public String getOfferAvailDate() {
        return offerAvailDate;
    }

    public void setOfferAvailDate(String offerAvailDate) {
        this.offerAvailDate = offerAvailDate;
    }
}
