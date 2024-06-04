package com.rew.multimodal_suite;

import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.SensorEvent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {


    //Accelerometer
    private TextView accelerometerXData;
    private TextView accelerometerYData;
    private TextView accelerometerZData;

    //TTS
    private TextToSpeechGenerator textToSpeechGenerator;
    private EditText ttsEditText;

    //SR
    private SpeechRecognitionGenerator speechRecognitionGenerator;
    private TextView srResult;
    private boolean srListening;

    //OCR
    private WritingRecognition writingRecognition;
    private TextView ocrResult;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Accelerometer
        new AccelerometerEventHandler(this, this::getAccelerometerSensorData);
        accelerometerXData = findViewById(R.id.accelerometerX);
        accelerometerYData = findViewById(R.id.accelerometerY);
        accelerometerZData = findViewById(R.id.accelerometerZ);


        //TTS
        textToSpeechGenerator = new TextToSpeechGenerator(this);
        ttsEditText = findViewById(R.id.ttsText);
        Button ttsConvertButton = findViewById(R.id.ttsConvert);

        ttsConvertButton.setOnClickListener((v)->{
            String text = String.valueOf(ttsEditText.getText());
            textToSpeechGenerator.speak(text);
        });

        //SR
        speechRecognitionGenerator = new SpeechRecognitionGenerator(this, this::getSRdata,this::srEndSpeechListener);
        srResult = findViewById(R.id.srResult);
        Button srSpeak = findViewById(R.id.srSpeak);
        srSpeak.setOnClickListener((v)-> {
            if(!srListening){
                speechRecognitionGenerator.startListening();
                srListening = true;
            }else{
                speechRecognitionGenerator.stopListening();
                srListening = false;
            }
        });

        //Writing Recognition
        writingRecognition = new WritingRecognition(this, WritingRecognition.RecognitionType.ALL);
        Button ocrImportButton = findViewById(R.id.ocrImportImage);
        ocrResult = findViewById(R.id.ocrResult);

        ActivityResultLauncher<Intent> importActivityResult = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        try {
                            assert result.getData() != null;
                            Uri imageUri = result.getData().getData();
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                            String resultText = writingRecognition.extractText(bitmap);
                            ocrResult.setText(resultText);
                        } catch (IOException e) {
                            Log.d("Exception", "Import image :" + e.getMessage());
                        }
                    }
                });


        ocrImportButton.setOnClickListener((v)->{
            Intent mediaIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            importActivityResult.launch(mediaIntent);
        });
    }

    public void getAccelerometerSensorData(SensorEvent sensor){
        accelerometerXData.setText(String.format(Locale.getDefault(),"%.2f", sensor.values[0]));
        accelerometerYData.setText(String.format(Locale.getDefault(),"%.2f", sensor.values[1]));
        accelerometerZData.setText(String.format(Locale.getDefault(),"%.2f", sensor.values[2]));
    }

    public void getSRdata(String data){
        srResult.setText(data);
    }

    public void srEndSpeechListener(){
        srListening = false;
    }

}