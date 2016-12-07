package models;

/**
 * Created by Macbook on 02/10/16.
 */
import com.google.gson.JsonArray;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/** Implement this class from "Serializable"
 * So that you can pass this class Object to another using Intents
 * Otherwise you can't pass to another actitivy
 * */
public class PlaceDetails {

    public void setStatus(String status) {
        this.status = status;
    }

    @SerializedName("formatted_address")
    public String formatted_address;

    @SerializedName("formatted_phone_number")
    public String formatted_phone_number;

    @SerializedName("place_id")
    public String place_id;

    @SerializedName("vicinity")
    public String vicinity;

    @SerializedName("geometry")
    public Geometry geometry;

    @SerializedName("address_components")
    public JsonArray address_components;

    @SerializedName("status")
    public String status;

    @SerializedName("name")
    public String name;

    @SerializedName("website")
    public String website;

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getName() {
        return name;
    }

    public String getPlace_id() {
        return place_id;
    }

    public void setPlace_id(String place_id) {
        this.place_id = place_id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static class Geometry implements Serializable
    {
        @SerializedName("location")
        public Location location;
    }

    public static class Location implements Serializable
    {
        @SerializedName("lat")
        public double lat;

        @SerializedName("lng")
        public double lng;

        public double getLat() {
            return lat;
        }

        public double getLng() {
            return lng;
        }

    }

    public String getFormatted_address() {
        return formatted_address;
    }

    public void setFormatted_address(String formatted_address) {
        this.formatted_address = formatted_address;
    }

    public String getFormatted_phone_number() {
        return formatted_phone_number;
    }

    public void setFormatted_phone_number(String formatted_phone_number) {
        this.formatted_phone_number = formatted_phone_number;
    }

    public String getVicinity() {
        return vicinity;
    }

    public void setVicinity(String vicinity) {
        this.vicinity = vicinity;
    }

    public JsonArray getAddress_components() {
        return address_components;
    }

    public void setAddress_components(JsonArray address_components) {
        this.address_components = address_components;
    }

    public String getStatus() {
        return status;
    }
}