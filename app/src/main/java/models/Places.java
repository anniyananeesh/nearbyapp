package models;

/**
 * Created by Macbook on 02/10/16.
 */
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/** Implement this class from "Serializable"
 * So that you can pass this class Object to another using Intents
 * Otherwise you can't pass to another actitivy
 * */
public class Places {

    @SerializedName("id")
    public String id;

    @SerializedName("name")
    public String name;

    @SerializedName("reference")
    public String reference;

    @SerializedName("icon")
    public String icon;

    @SerializedName("vicinity")
    public String vicinity;

    @SerializedName("geometry")
    public Geometry geometry;

    @SerializedName("formatted_address")
    public String formatted_address;

    @SerializedName("formatted_phone_number")
    public String formatted_phone_number;

    @SerializedName("status")
    public String status;

    @SerializedName("distance")
    public String distance;

    @SerializedName("place_id")
    public String place_id;

    public String getPlace_id() {
        return place_id;
    }

    public void setPlace_id(String place_id) {
        this.place_id = place_id;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getVicinity() {
        return vicinity;
    }

    public void setVicinity(String vicinity) {
        this.vicinity = vicinity;
    }


    public String getFormatted_address() {
        return formatted_address;
    }

    public void setFormatted_address(String formatted_address) {
        this.formatted_address = formatted_address;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return name + " - " + id + " - " + reference;
    }

    public static class Geometry implements Serializable
    {
        @SerializedName("location")
        public Location location;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

}