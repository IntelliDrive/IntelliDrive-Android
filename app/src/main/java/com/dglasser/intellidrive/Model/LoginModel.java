package com.dglasser.intellidrive.Model;

import com.dglasser.intellidrive.POJO.LoginObject;
import com.dglasser.intellidrive.POJO.Token;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Interface for making networking requests. Used for logging in.
 */
public interface LoginModel {
    String ENDPOINT = "http://intellidriveapp.com/";

    @POST("api/login/")
    Call<Token> requestLoginToken(@Body LoginObject login);

    @GET("/api/login/")
    Call<Token> requestLoginToken(
        @Query("email") String email,
        @Query("password") String password);
}
