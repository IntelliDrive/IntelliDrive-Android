package com.dglasser.intellidrive.Model;

import com.dglasser.intellidrive.POJO.BaseTripResponse;
import com.dglasser.intellidrive.POJO.RobustTripResponse;
import com.dglasser.intellidrive.POJO.Token;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Interface for making networking requests. Used for sending miles and receiving trip data.
 */
public interface TripModel {

    String NEW_TRIP_ENDPOINT = "http://intellidriveapp.com/api/mile/";

    @POST("{type}/{name}/new/")
    Call<BaseTripResponse> initNewTrip(
        @Path("type") String type,
        @Path("name") String name,
        @Body Token token
    );

    @POST("{miles}/add/")
    Call<BaseTripResponse> addTripMiles(
        @Path("miles") String miles,
        @Body Token token
    );

    @POST("/.")
    Call<RobustTripResponse> getAllMiles(
        @Body Token token
    );

    @POST("{type}/")
    Call<RobustTripResponse> getMiles(
        @Path("type") String type,
        @Body Token token
    );

}
