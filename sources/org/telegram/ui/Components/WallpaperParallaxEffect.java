package org.telegram.ui.Components;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.WindowManager;
import org.telegram.messenger.AndroidUtilities;

public class WallpaperParallaxEffect implements SensorEventListener {
    private Sensor accelerometer;
    private int bufferOffset;
    private Callback callback;
    private boolean enabled;
    private float[] pitchBuffer = new float[3];
    private float[] rollBuffer = new float[3];
    private SensorManager sensorManager;
    private WindowManager wm;

    public interface Callback {
        void onOffsetsChanged(int i, int i2);
    }

    public WallpaperParallaxEffect(Context context) {
        this.wm = (WindowManager) context.getSystemService("window");
        this.sensorManager = (SensorManager) context.getSystemService("sensor");
        this.accelerometer = this.sensorManager.getDefaultSensor(1);
    }

    public void setEnabled(boolean enabled) {
        if (this.enabled != enabled) {
            this.enabled = enabled;
            if (this.accelerometer != null) {
                if (enabled) {
                    this.sensorManager.registerListener(this, this.accelerometer, 1);
                } else {
                    this.sensorManager.unregisterListener(this);
                }
            }
        }
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public float getScale(int boundsWidth, int boundsHeight) {
        int offset = AndroidUtilities.dp(16.0f);
        return Math.max((((float) boundsWidth) + ((float) (offset * 2))) / ((float) boundsWidth), (((float) boundsHeight) + ((float) (offset * 2))) / ((float) boundsHeight));
    }

    public void onSensorChanged(SensorEvent event) {
        float y = event.values[1] / 9.80665f;
        float z = event.values[2] / 9.80665f;
        float roll = (float) ((Math.atan2((double) y, (double) z) / 3.141592653589793d) * 2.0d);
        float pitch = (float) ((Math.atan2((double) (-(event.values[0] / 9.80665f)), Math.sqrt((double) ((y * y) + (z * z)))) / 3.141592653589793d) * 2.0d);
        float tmp;
        switch (this.wm.getDefaultDisplay().getRotation()) {
            case 1:
                tmp = pitch;
                pitch = roll;
                roll = tmp;
                break;
            case 2:
                roll = -roll;
                pitch = -pitch;
                break;
            case 3:
                tmp = -pitch;
                pitch = roll;
                roll = tmp;
                break;
        }
        this.rollBuffer[this.bufferOffset] = roll;
        this.pitchBuffer[this.bufferOffset] = pitch;
        this.bufferOffset = (this.bufferOffset + 1) % this.rollBuffer.length;
        pitch = 0.0f;
        roll = 0.0f;
        for (int i = 0; i < this.rollBuffer.length; i++) {
            roll += this.rollBuffer[i];
            pitch += this.pitchBuffer[i];
        }
        roll /= (float) this.rollBuffer.length;
        pitch /= (float) this.rollBuffer.length;
        if (roll > 1.0f) {
            roll = 2.0f - roll;
        } else if (roll < -1.0f) {
            roll = -2.0f - roll;
        }
        int offsetX = Math.round(AndroidUtilities.dpf2(16.0f) * pitch);
        int offsetY = Math.round(AndroidUtilities.dpf2(16.0f) * roll);
        if (this.callback != null) {
            this.callback.onOffsetsChanged(offsetX, offsetY);
        }
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}
