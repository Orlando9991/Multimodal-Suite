package com.rew.multimodal_suite;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.util.function.Consumer;

public class AccelerometerEventHandler implements SensorEventListener {
    private final Consumer<SensorEvent> consumer;

    public AccelerometerEventHandler(Context context, Consumer<SensorEvent> consumer){
        SensorManager sensorManager = (SensorManager) (context.getSystemService(Context.SENSOR_SERVICE));
        Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_GAME);
        this.consumer = consumer;
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        consumer.accept(event);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
