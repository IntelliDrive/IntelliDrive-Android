package com.dglasser.intellidrive.Model;

import com.dglasser.intellidrive.POJO.LoginObject;
import com.dglasser.intellidrive.POJO.Token;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Interface for making networking requests. Used for logging in.
 */
public interface LoginModel {
    String ENDPOINT = "http://intellidriveapp.com/";

    @POST("api/login/")
    Call<Token> requestLoginToken(@Body LoginObject login);
}
