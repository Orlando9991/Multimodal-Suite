package com.rew.multimodal_suite;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class WritingRecognition {
    private static final String NUMBERS_REC = "0123456789";
    private static final String CHARACTERS_REC = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmopqrstuvwxyz";

    public enum RecognitionType{
        NUMBERS(NUMBERS_REC, CHARACTERS_REC),
        CHARACTERS(CHARACTERS_REC, NUMBERS_REC),
        ALL("","");

        private final String whiteList;
        private final String blackList;
        RecognitionType(String whiteList, String blackList) {
            this.whiteList = whiteList;
            this.blackList = blackList;
        }
    }

    private final String SUBFOLDER_NAME = "tessdata";
    private final String FILE_NAME = "eng.traineddata";
    private final String FILE_PATH = SUBFOLDER_NAME +"/" + FILE_NAME;
    private String data_path;
    private final TessBaseAPI tessBaseAPI;
    private Context context;
    private RecognitionType recognitionType;

    public WritingRecognition(AppCompatActivity activity, RecognitionType recognitionType, int pagSeqMode) {
        init(activity, recognitionType);
        tessBaseAPI = initTessBase(pagSeqMode);
    }

    public WritingRecognition(AppCompatActivity activity, RecognitionType recognitionType) {
        init(activity, recognitionType);
        tessBaseAPI = initTessBase(-1);
    }

    public void init(AppCompatActivity activity, RecognitionType recognitionType){
        this.context = activity.getApplicationContext();
        this.recognitionType = recognitionType;

        //Get permissions
        getPermissions(activity);

        //Save trained data file in device storage
        saveTemporaryFileToStorage();

        data_path = context.getFilesDir().getAbsolutePath() + "/";
    }

    public void getPermissions(AppCompatActivity activity){
        if (ContextCompat.checkSelfPermission(
                activity.getApplicationContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
    }


    public String extractText(Bitmap bitmap) {
        tessBaseAPI.clear();
        try {
            tessBaseAPI.setImage(bitmap);
        }catch (Exception e){
            Log.e("tessBaseAPI", "Can´t read bitmap", e);
        }
        return tessBaseAPI.getUTF8Text();
    }

    private TessBaseAPI initTessBase(int pagMode){
        TessBaseAPI tessBaseAPI = new TessBaseAPI();
        tessBaseAPI.init(data_path, "eng");
        tessBaseAPI.setVariable(TessBaseAPI.VAR_CHAR_WHITELIST, recognitionType.whiteList);
        tessBaseAPI.setVariable(TessBaseAPI.VAR_CHAR_BLACKLIST,recognitionType.blackList);
        if(pagMode!=-1){
            tessBaseAPI.setPageSegMode(pagMode);
        }
        return tessBaseAPI;
    }

    public void destroy(){
        if(this.tessBaseAPI!=null){
            tessBaseAPI.recycle();
        }
    }

    private void saveTemporaryFileToStorage(){
        AssetManager assetManager = context.getAssets();
        InputStream inputStream;
        FileOutputStream outputStream;

        try {
            //Get file data
            inputStream = assetManager.open(FILE_PATH);

            //Create subfolder if not exists
            File tessDirectory = new File(context.getFilesDir(), SUBFOLDER_NAME);
            if (!tessDirectory.exists()) {
                boolean created = tessDirectory.mkdirs();
                if(!created)
                    throw  new RuntimeException("Error creating folder");
            }

            //Output file (copy from assets folder)
            File outputFile = new File(tessDirectory, FILE_NAME);
            outputStream = new FileOutputStream(outputFile);

            //Clone data
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
            //Close stream
            inputStream.close();
            outputStream.close();

        } catch (IOException e) {
            Log.e("FileSaveError", "Error saving temporary file", e);
            throw new RuntimeException("Error saving temporary file", e);
        }
    }

}
