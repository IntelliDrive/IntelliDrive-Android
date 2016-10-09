package com.dglasser.intellidrive;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingsActivity extends AppCompatActivity {

    /**
     * Save button.
     */
    @BindView(R.id.save_button) Button saveButton;

    /**
     * Gallons per car.
     */
    @BindView(R.id.gpc) EditText gallonsPerCar;

    /**
     * Shared preferences.
     */
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);

        sharedPreferences = getSharedPreferences(getString(R.string.token_storage), MODE_PRIVATE);
        String gpc = sharedPreferences.getString(getString(R.string.gallons_save), "25");
        gallonsPerCar.setText(gpc);

        saveButton.setOnClickListener(v -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(getString(R.string.gallons_save), gallonsPerCar.getText().toString());
            editor.apply();
            Toast.makeText(this, "Saved!", Toast.LENGTH_SHORT).show();
        });
    }
}
