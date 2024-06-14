package com.rew.multimodal_suite;

import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class TextToSpeechGenerator {

    private final TextToSpeech textToSpeech;

    public TextToSpeechGenerator(AppCompatActivity activity) {
        textToSpeech = setUpTextToSpeech(activity, new Locale("pt","PT"));

    }

    public TextToSpeechGenerator(AppCompatActivity activity, Locale locale) {
        textToSpeech = setUpTextToSpeech(activity, locale);
    }

    private TextToSpeech setUpTextToSpeech(AppCompatActivity activity, Locale locale){
        return new TextToSpeech(activity.getApplicationContext(), status -> {
            if (status == TextToSpeech.SUCCESS) {
                int result = textToSpeech.setLanguage(locale);
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e("TTS", "This Language is not available, attempting download");
                    Intent installIntent = new Intent();
                    installIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                    activity.startActivity(installIntent);
                }
            }
            else {
                Log.e("TTS", "Initialization Failed!");
            }
        }, "com.google.android.tts");
    }

    public void speak(String phrase){
        textToSpeech.speak(phrase, TextToSpeech.QUEUE_FLUSH, null, null);
    }

}
