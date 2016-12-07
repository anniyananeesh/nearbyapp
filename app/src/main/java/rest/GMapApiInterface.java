package rest;

import models.PlaceDetailsResponse;
import models.PlacesResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;


public interface GMapApiInterface {

    @GET("nearbysearch/json")
    Call<PlacesResponse> getNearbyUsers(@Query("key") String apiKey, @Query("rankby") String rankBy, @Query("location") String location, @Query("types") String type);

    @GET("details/json")
    Call<PlaceDetailsResponse> getPlaceDetials(@Query("key") String apiKey, @Query("placeid") String placeId);

}