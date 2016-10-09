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
import com.dglasser.intellidrive.Model.RegisterModel;
import com.dglasser.intellidrive.POJO.RegisterObject;
import com.dglasser.intellidrive.POJO.BoringResposeObject;
import com.google.gson.Gson;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Page where user registers.
 */
public class RegisterActivity extends AppCompatActivity implements Callback<BoringResposeObject> {

    private static final int LOCATION_REQUEST_CODE = 0;

    /**
     * Field where user inputs name.
     */
    @BindView(R.id.name_field) TextInputEditText nameField;

    /**
     * Field where user inputs username.
     */
    @BindView(R.id.username_field) TextInputEditText emailField;

    /**
     * Field where user inputs password.
     */
    @BindView(R.id.password_field) TextInputEditText passwordField;

    /**
     * Button user presses to register.
     */
    @BindView(R.id.forward_button) Button registerButton;

    /**
     * Login button.
     */
    @BindView(R.id.login_button) Button loginButton;

    /**
     * Android shared preferences.
     */
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);

        ActivityCompat.requestPermissions(
            this,
            new String[] {Manifest.permission.ACCESS_FINE_LOCATION},
            LOCATION_REQUEST_CODE);

        // If we're logged in, jump straight to the main page.
        sharedPreferences = getApplicationContext().getSharedPreferences(
            getString(R.string.token_storage), Context.MODE_PRIVATE);

        if (sharedPreferences.getString(getString(R.string.token_save), null) != null) {
            startActivity(new Intent(this, MainActivity.class));
        }

        Gson gson = new Gson();

        Retrofit retrofit =  new Retrofit.Builder()
            .baseUrl(LoginModel.ENDPOINT)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build();

        RegisterModel register = retrofit.create(RegisterModel.class);

        registerButton.setOnClickListener(v -> {
            ActivityCompat.requestPermissions(
                this,
                new String[] {Manifest.permission.ACCESS_FINE_LOCATION},
                LOCATION_REQUEST_CODE);

            String name = nameField.getText().toString();
            String email = emailField.getText().toString();
            String password = passwordField.getText().toString();
            if (!name.isEmpty() || !email.isEmpty() || !password.isEmpty()) {
                Call<BoringResposeObject> call = register.registerNewUser(
                    new RegisterObject(name, email, password));

                call.clone().enqueue(this);
            } else {
                Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
            }
        });

        loginButton.setOnClickListener(v -> startActivity(new Intent(this, LoginActivity.class)));
    }

    @Override
    public void onResponse(Call<BoringResposeObject> call, Response<BoringResposeObject> response) {
        if (response.code() == 200) {
            Toast.makeText(
                this,
                "Registered successfully!",
                Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.putExtra(getString(R.string.email), emailField.getText().toString());
            intent.putExtra(getString(R.string.password), passwordField.getText().toString());
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(
                this,
                "Error while registering. Please check your network connection and try again",
                Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onFailure(Call<BoringResposeObject> call, Throwable t) {
        Toast.makeText(
            this,
            "Error while registering. Please check your network connection and try again",
            Toast.LENGTH_LONG).show();
    }
}
