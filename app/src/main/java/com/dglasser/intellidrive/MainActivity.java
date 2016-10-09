package com.dglasser.intellidrive;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.dglasser.intellidrive.CleverBotInterface.ChatterBot;
import com.dglasser.intellidrive.CleverBotInterface.ChatterBotFactory;
import com.dglasser.intellidrive.CleverBotInterface.ChatterBotSession;
import com.dglasser.intellidrive.CleverBotInterface.ChatterBotType;
import com.dglasser.intellidrive.CustomDialogs.TripDialogFragment;
import com.dglasser.intellidrive.Events.ThinkEvent;
import com.dglasser.intellidrive.Model.TripModel;
import com.dglasser.intellidrive.POJO.BaseTripResponse;
import com.dglasser.intellidrive.POJO.RobustTripResponse;
import com.dglasser.intellidrive.POJO.Token;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * MainActivity. You already know.
 */
public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener, LocationListener, Callback<BaseTripResponse> {

    /**
     * Google API client.
     */
    GoogleApiClient googleApiClient;

    /**
     * Current location.
     */
    Location location;

    /**
     * Location request scheduler.
     */
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
     * Button to initiate talk with chatbot.
     */
    @BindView(R.id.voice_button) ImageButton voiceButton;

    /**
     * Starts trip.
     */
    @BindView(R.id.start_trip_button) Button startTripButton;

    /**
     * Shows number of miles travelled.
     */
    @BindView(R.id.miles_travelled_view) TextView milesTravelledView;

    /**
     * TextView with the miles per gallon.
     */
    @BindView(R.id.mpg) TextView mpgView;

    /**
     * TextView with the cost per gallon.
     */
    @BindView(R.id.cpg) TextView cpgView;

    /**
     * Shared preferences instance.
     */
    SharedPreferences preferences;

    /**
     * Text to speech interpreter.
     */
    TextToSpeech ttsInterpreter;

    /**
     * Chatbot instance.
     */
    ChatterBot bot;

    /**
     * TimerTask.
     */
    private TimerTask timerTask;

    /**
     * Timer.
     */
    private Timer timer;

    /**
     * Android handler.
     */
    private Handler handler = new Handler();

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

        startMileReporter();

        locationRequest = new LocationRequest();

        locationRequest.setInterval(15000);

        preferences = getSharedPreferences(getString(R.string.token_storage), MODE_PRIVATE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        initTextToSpeech();
        Gson gson = new Gson();

        double milesFilled;

        try {
            milesFilled = Double.valueOf(
                preferences.getString(getString(R.string.miles_filled_save), "0"));
        } catch (NullPointerException e) {
            milesFilled = 0;
        }

        double cost;

        try {
            cost = milesFilled *
                Double.valueOf(preferences.getString(getString(R.string.cost), "2.20"));
        } catch (NullPointerException e) {
            cost = 2.20;
        }

        String gallons;

        try {
            gallons = preferences.getString(getString(R.string.gallons_in_car), "25");
        } catch (NullPointerException e) {
            gallons = "25";
        }

        double mpg = miles / Double.valueOf(gallons);

        mpgView.setText(String.format(getString(R.string.mpg), mpg == 0 ? "∞" : mpg));
        cpgView.setText(String.format(getString(R.string.cpg), cost + ""));
        milesTravelledView.setText(String.format(getString(R.string.miles_travelled), miles + ""));

        ChatterBotFactory factory = new ChatterBotFactory();
        try {
            bot = factory.create(ChatterBotType.CLEVERBOT);
        } catch (Exception e) {
            e.printStackTrace();
        }

        startTripButton.setOnClickListener(v -> {
            Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(TripModel.NEW_TRIP_ENDPOINT)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

            TripModel model = retrofit.create(TripModel.class);
            TripDialogFragment tripDialogFragment = TripDialogFragment.newInstance("new trip");
            tripDialogFragment.show(getSupportFragmentManager(), "test");

            tripDialogFragment.setListener(pair -> {
                Call<BaseTripResponse> call = model.initNewTrip(
                    pair.getTripType(),
                    pair.getName(),
                    new Token(preferences.getString(getString(R.string.token_save), null)));

                call.clone().enqueue(this);
            });
        });

        voiceButton.setOnClickListener(v -> sendSpeechInput());
    }

    /**
     * Event listener for {@link ThinkEvent}.
     * @param event ThinkEvent.
     */
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
        googleApiClient.disconnect();
        stopTimerTask();
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
        milesTravelledView.setText(String.format(getString(R.string.miles_travelled), miles + ""));
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
    }

    @Override
    public void onResponse(Call<BaseTripResponse> call, Response<BaseTripResponse> response) {
        if (response.body() instanceof RobustTripResponse) {
            // no op for now
        } else {
            if (response.code() == 200) {
                Log.v("DGL", response.body().getMsg());
            } else {
            }
        }
    }

    @Override
    public void onFailure(Call<BaseTripResponse> call, Throwable t) {
        // no-op
    }

    /**
     * Starts the mile reporter.
     */
    private void startMileReporter() {
        timer = new Timer();
        initializeMileReporter();
        timer.schedule(timerTask, 1000, 600000);
    }

    /**
     * Initializes critical parts of the mile reporter, and schedules it to run. This should
     * <b>NEVER</b> be called outside of startMileReporter unless you're looking for NPes.
     */
    private void initializeMileReporter() {
        Gson gson = new Gson();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(() -> {
                    Retrofit retrofit = new Retrofit
                        .Builder()
                        .baseUrl(TripModel.NEW_TRIP_ENDPOINT)
                        .addConverterFactory(GsonConverterFactory.create(gson))
                        .build();

                    TripModel tripModel = retrofit.create(TripModel.class);
                    Call<BaseTripResponse> call = tripModel.addTripMiles(
                        miles + "",
                        new Token(preferences.getString(getString(R.string.token_save), null)));

                    call.clone().enqueue(MainActivity.this);
                });
            }
        };
        timerTask.run();
    }

    /**
     * Stops the timer task. Who likes memory leaks? Crazy people that's who.
     */
    private void stopTimerTask() {
        if (timerTask != null) {
            timerTask.cancel();
            timer.purge();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        startActivity(new Intent(this, SettingsActivity.class));
        return super.onOptionsItemSelected(item);
    }
}
