package com.dglasser.intellidrive;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.SystemClock;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.Toast;

import com.dglasser.intellidrive.CleverBotInterface.ChatterBot;
import com.dglasser.intellidrive.CleverBotInterface.ChatterBotFactory;
import com.dglasser.intellidrive.CleverBotInterface.ChatterBotSession;
import com.dglasser.intellidrive.CleverBotInterface.ChatterBotType;
import com.dglasser.intellidrive.Events.ThinkEvent;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener, LocationListener {

    GoogleApiClient googleApiClient;

    /**
     * Current location.
     */
    Location location;

    LocationRequest locationRequest;

    /**
     * Number of miles traveled.
     */
    float miles = 0;

    /**
     * Speech request code.
     */
    private static final int SPEECH_REQUEST_CODE = 0;

    /**
     * Location request code.
     */
    private static final int LOCATION_REQUEST_CODE = 1;

    /**
     * Button to initiate talk with chatbot.
     */
    @BindView(R.id.voice_button) ImageButton voiceButton;

    /**
     * Text to speech interpreter.
     */
    TextToSpeech ttsInterpreter;

    /**
     * Chatbot instance.
     */
    ChatterBot bot;

    @Override
    protected void onStart() {
        super.onStart();
        Log.wtf("DGL", "Calling onStart");
        EventBus.getDefault().register(this);
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

            googleApiClient.connect();
            Log.wtf("DGL", "Finished building API client");
        }

        locationRequest = new LocationRequest();

        locationRequest.setInterval(15000);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        initTextToSpeech();

        ChatterBotFactory factory = new ChatterBotFactory();
        try {
            bot = factory.create(ChatterBotType.CLEVERBOT);
        } catch (Exception e) {
            e.printStackTrace();
        }

        voiceButton.setOnClickListener(v -> sendSpeechInput());
    }

    @SuppressWarnings("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ThinkEvent event) {
        String utteranceId = this.hashCode() + "";
        ttsInterpreter.speak(event.getThought(), TextToSpeech.QUEUE_FLUSH, null, utteranceId);
        SystemClock.sleep(2500);
        sendSpeechInput();
    }

    /**
     * Gets speech input from mic, and then passes it to speech interpreter.
     */
    private void sendSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

        startActivityForResult(intent, SPEECH_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK) {

            Thread t = new Thread(() -> {
                ChatterBotSession bot1session = bot.createSession();
                if (bot1session != null) {
                    try {
                        String userInput =
                            data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS).get(0);
                        if (!userInput.contains("stop")) {
                            bot1session.think(userInput);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            t.start();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    /**
     * Initializes text to speech interpreter.
     */
    private void initTextToSpeech() {
        ttsInterpreter = new TextToSpeech(getApplicationContext(), status -> {
            if(status != TextToSpeech.ERROR) {
                ttsInterpreter.setLanguage(Locale.US);
            }
        });
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.wtf("DGL", "Now connected");
        if (ActivityCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Log.wtf("DGL", "We have permission");

            LocationServices.FusedLocationApi.requestLocationUpdates(
                googleApiClient, locationRequest, this);


            location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        } else {
            Toast.makeText(this, "didn't work?", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        // no op, idgaf
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.wtf("DGL", "Failed somewhere");
        Toast.makeText(
            this,
            "Can't get location: Location will need to be input manually",
            Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLocationChanged(Location location) {
        if (this.location == null) {
            this.location = location;
        }
        miles += this.location.distanceTo(location) * 0.000621371;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
    }
}
