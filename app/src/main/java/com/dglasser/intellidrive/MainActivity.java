package com.dglasser.intellidrive;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageButton;
import android.widget.Toast;

import com.dglasser.intellidrive.CleverBotInterface.ChatterBot;
import com.dglasser.intellidrive.CleverBotInterface.ChatterBotFactory;
import com.dglasser.intellidrive.CleverBotInterface.ChatterBotSession;
import com.dglasser.intellidrive.CleverBotInterface.ChatterBotType;
import com.dglasser.intellidrive.Events.ThinkEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    /**
     * Speech request code.
     */
    private static final int SPEECH_REQUEST_CODE = 0;

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
        EventBus.getDefault().register(this);
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
        Toast.makeText(this, event.getThought(), Toast.LENGTH_SHORT).show();
        String utteranceId=this.hashCode() + "";
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
}
