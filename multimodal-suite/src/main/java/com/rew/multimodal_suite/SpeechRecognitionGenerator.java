package com.rew.multimodal_suite;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.function.Consumer;

public class SpeechRecognitionGenerator {
    private final Intent speechRecognizerIntent;
    private SpeechRecognizer speechRecognizer;

    public SpeechRecognitionGenerator(AppCompatActivity activity, Intent intent, Consumer<String> consumer, Runnable runnable) {
        init(activity, consumer, runnable);
        speechRecognizerIntent = intent;
    }

    public SpeechRecognitionGenerator(AppCompatActivity activity, Consumer<String> consumer, Runnable runnable) {
        init(activity, consumer, runnable);
        speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
    }

    private void init(AppCompatActivity activity, Consumer<String> consumer, Runnable runnable){
        //Get permissions
        getPermissions(activity);

        //init speechRecognizer
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(activity.getApplicationContext());
        speechRecognizer.setRecognitionListener(recognitionListenerSetUp(consumer, runnable));
    }

    public void getPermissions(AppCompatActivity activity){
        if (ContextCompat.checkSelfPermission(
                activity.getApplicationContext(),
                android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{android.Manifest.permission.RECORD_AUDIO}, 1);
        }
    }

    public void startListening(){
        speechRecognizer.startListening(speechRecognizerIntent);
    }

    public void stopListening(){
        speechRecognizer.stopListening();
    }


    public RecognitionListener recognitionListenerSetUp(Consumer<String> stringConsumer, Runnable runnable){
        return new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {
                //Log.d("", "onReadyForSpeech");
            }

            @Override
            public void onBeginningOfSpeech() {
                //Log.d("", "onBeginningOfSpeech");
            }

            @Override
            public void onRmsChanged(float msdB) {
                //Log.d("", "onRmsChanged");
            }

            @Override
            public void onBufferReceived(byte[] buffer) {
                //Log.d("", "onBufferReceived");
            }

            @Override
            public void onEndOfSpeech() {
                //resetData
                runnable.run();
            }

            @Override
            public void onError(int error) {
                //Log.d("", "onError");
            }

            @Override
            public void onResults(Bundle results) {
                ArrayList<String> data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                stringConsumer.accept(data.get(0));
            }

            @Override
            public void onPartialResults(Bundle partialResults) {
                //Log.d("", "onPartialResults");
            }

            @Override
            public void onEvent(int eventType, Bundle params) {
                //Log.d("", "onEvent");
            }
        };
    }

}
