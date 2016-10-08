package com.dglasser.intellidrive.Model;

import com.dglasser.intellidrive.POJO.RegisterObject;
import com.dglasser.intellidrive.POJO.BoringResposeObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Interface for making networking requests. Used for registering.
 */
public interface RegisterModel {
    String ENDPOINT = "http://intellidrive.com/";

    @POST("/api/register/")
    Call<BoringResposeObject> registerNewUser(@Body RegisterObject login);

    @POST("/api/register/")
    Call<BoringResposeObject> registerNewUser(
        @Query("name") String name,
        @Query("email") String email,
        @Query("password") String password);


}
