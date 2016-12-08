package rest;

import models.NotificationsCategoryResponse;
import models.OfferResponse;
import models.OfferResponseJson;
import models.ReviewsResponse;
import models.UserResponse;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;


public interface ApiInterface {


    @GET("offers")
    Call<OfferResponse> getOffersByLocation(@Query("type") String type, @Query("lat") String latitude, @Query("lng") String longitude, @Query("md") String minDistance, @Query("last_offer_id") String lastOfferID);

    @FormUrlEncoded
    @POST("add_profile")
    Call<UserResponse> saveUser(@Field("name") String name,
                                @Field("category") String category,
                                @Field("email") String email,
                                @Field("phone") String phone,
                                @Field("latitude") String latitude,
                                @Field("longitude") String longitude);

    @FormUrlEncoded
    @POST("review")
    Call<UserResponse> saveRating(@Field("subscriber_id") String subscriber,
                                  @Field("name") String name,
                                  @Field("email") String email,
                                  @Field("image") String image,
                                  @Field("rating") String rating,
                                  @Field("message") String message,
                                  @Field("gender") String gender,
                                  @Field("facebook_id") String facebook_id);

    @GET("user")
    Call<UserResponse> getUser(@Query("place_id") String placeId);

    @GET("get_offers_by_user")
    Call<OfferResponse> getOffersByUser(@Query("user_id") String userId);

    @GET("reviews")
    Call<ReviewsResponse> getReviewsByUser(@Query("user_id") String userId);

    @GET("get_offer")
    Call<OfferResponseJson> getOffersById(@Query("offer_id") String offerId);

    @FormUrlEncoded
    @POST("offer_request")
    Call<UserResponse> sendGetOffer(@Field("subscriber_id") String subscriber,
                                    @Field("offer_id") String offerId,
                                    @Field("offer_title") String offerTitle,
                                    @Field("username") String name,
                                    @Field("email") String email,
                                    @Field("subsc_email") String subscEmail);

    @GET("get_all_offers")
    Call<NotificationsCategoryResponse> getAllOffers(@Query("last_offer") String offerId);

    @GET("get_unread_offers")
    Call<NotificationsCategoryResponse> getUnreadOffers(@Query("last_offer_id") String offerId);

}