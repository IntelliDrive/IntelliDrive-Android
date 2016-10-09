package com.dglasser.intellidrive;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.Toast;

import com.dglasser.intellidrive.Model.LoginModel;
import com.dglasser.intellidrive.POJO.LoginObject;
import com.dglasser.intellidrive.POJO.Token;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Activity where user is able to log in.
 */
public class LoginActivity extends AppCompatActivity implements Callback<Token> {

    /**
     * Location request code.
     */
    private static final int LOCATION_REQUEST_CODE = 1;

    /**
     * Button to log in.
     */
    @BindView(R.id.forward_button) Button forwardButton;

    /**
     * Field where user inputs username.
     */
    @BindView(R.id.username_field) TextInputEditText userNameField;

    /**
     * Field where user inputs password.
     */
    @BindView(R.id.password_field) TextInputEditText passwordField;

    /**
     * Shared preferences.
     */
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        String passedEmail = (String) getIntent().getExtras().get(getString(R.string.email));
        String passedPassword = (String) getIntent().getExtras().get(getString(R.string.password));

        // If we're logged in, jump straight to the main page.
        sharedPreferences = getApplicationContext().getSharedPreferences(
            getString(R.string.token_storage), Context.MODE_PRIVATE);

        if (sharedPreferences.getString(getString(R.string.token_save), null) != null) {
            startActivity(new Intent(this, MainActivity.class));
        }

        Gson gson = new GsonBuilder().create();

        Retrofit retrofit =  new Retrofit.Builder()
            .baseUrl(LoginModel.ENDPOINT)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build();

        LoginModel login = retrofit.create(LoginModel.class);

        if (passedEmail != null && passedPassword != null) {
            Call<Token> call = login.requestLoginToken(
                new LoginObject(passedEmail, passedPassword));

            call.clone().enqueue(this);
        }

        forwardButton.setOnClickListener(v -> {
            ActivityCompat.requestPermissions(
                this,
                new String[] {Manifest.permission.ACCESS_FINE_LOCATION},
                LOCATION_REQUEST_CODE);

            Call<Token> call = login.requestLoginToken(
                new LoginObject(userNameField.getText().toString(),
                    passwordField.getText().toString()));

            call.clone().enqueue(this);
        });

    }

    @Override
    public void onResponse(Call<Token> call, Response<Token> response) {
        if (response.code() == 200) {
            Toast.makeText(this, response.body().getToken(), Toast.LENGTH_SHORT).show();
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(getString(R.string.token_save), response.body().getToken());
            editor.apply();
            Intent intent = new Intent(this, MainActivity.class);
            finishAffinity();
            startActivity(intent);
        } else {
            Toast.makeText(this, response.code() + "", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onFailure(Call<Token> call, Throwable t) {
        Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show();
    }
}
