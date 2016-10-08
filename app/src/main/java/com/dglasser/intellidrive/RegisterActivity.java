package com.dglasser.intellidrive;

import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);

        Gson gson = new Gson();

        Retrofit retrofit =  new Retrofit.Builder()
            .baseUrl(LoginModel.ENDPOINT)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build();

        RegisterModel register = retrofit.create(RegisterModel.class);

        registerButton.setOnClickListener(v -> {
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
    }

    @Override
    public void onResponse(Call<BoringResposeObject> call, Response<BoringResposeObject> response) {
        if (response.code() == 200) {
            Toast.makeText(
                this,
                "Registered successfully! Please log in with your new account!",
                Toast.LENGTH_LONG).show();

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
