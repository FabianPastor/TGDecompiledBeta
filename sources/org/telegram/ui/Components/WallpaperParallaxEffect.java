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
        void onOffsetsChanged(int i, int i2, float f);
    }

    public WallpaperParallaxEffect(Context context) {
        this.wm = (WindowManager) context.getSystemService("window");
        SensorManager sensorManager2 = (SensorManager) context.getSystemService("sensor");
        this.sensorManager = sensorManager2;
        this.accelerometer = sensorManager2.getDefaultSensor(1);
    }

    public void setEnabled(boolean enabled2) {
        if (this.enabled != enabled2) {
            this.enabled = enabled2;
            Sensor sensor = this.accelerometer;
            if (sensor != null) {
                if (enabled2) {
                    this.sensorManager.registerListener(this, sensor, 1);
                } else {
                    this.sensorManager.unregisterListener(this);
                }
            }
        }
    }

    public void setCallback(Callback callback2) {
        this.callback = callback2;
    }

    public float getScale(int boundsWidth, int boundsHeight) {
        int offset = AndroidUtilities.dp(16.0f);
        return Math.max((((float) boundsWidth) + ((float) (offset * 2))) / ((float) boundsWidth), (((float) boundsHeight) + ((float) (offset * 2))) / ((float) boundsHeight));
    }

    public void onSensorChanged(SensorEvent event) {
        SensorEvent sensorEvent = event;
        int rotation = this.wm.getDefaultDisplay().getRotation();
        float x = sensorEvent.values[0] / 9.80665f;
        float y = sensorEvent.values[1] / 9.80665f;
        float z = sensorEvent.values[2] / 9.80665f;
        float pitch = (float) ((Math.atan2((double) x, Math.sqrt((double) ((y * y) + (z * z)))) / 3.141592653589793d) * 2.0d);
        float roll = (float) ((Math.atan2((double) y, Math.sqrt((double) ((x * x) + (z * z)))) / 3.141592653589793d) * 2.0d);
        switch (rotation) {
            case 1:
                float tmp = pitch;
                pitch = roll;
                roll = tmp;
                break;
            case 2:
                roll = -roll;
                pitch = -pitch;
                break;
            case 3:
                float tmp2 = -pitch;
                pitch = roll;
                roll = tmp2;
                break;
        }
        float[] fArr = this.rollBuffer;
        int i = this.bufferOffset;
        fArr[i] = roll;
        this.pitchBuffer[i] = pitch;
        this.bufferOffset = (i + 1) % fArr.length;
        float pitch2 = 0.0f;
        float roll2 = 0.0f;
        int i2 = 0;
        while (true) {
            float[] fArr2 = this.rollBuffer;
            if (i2 < fArr2.length) {
                roll2 += fArr2[i2];
                pitch2 += this.pitchBuffer[i2];
                i2++;
            } else {
                float roll3 = roll2 / ((float) fArr2.length);
                float pitch3 = pitch2 / ((float) fArr2.length);
                if (roll3 > 1.0f) {
                    roll3 = 2.0f - roll3;
                } else if (roll3 < -1.0f) {
                    roll3 = -2.0f - roll3;
                }
                int offsetX = Math.round(AndroidUtilities.dpf2(16.0f) * pitch3);
                int offsetY = Math.round(AndroidUtilities.dpf2(16.0f) * roll3);
                float vx = Math.max(-1.0f, Math.min(1.0f, (-pitch3) / 0.45f));
                float vy = Math.max(-1.0f, Math.min(1.0f, (-roll3) / 0.45f));
                float len = (float) Math.sqrt((double) ((vx * vx) + (vy * vy)));
                float vx2 = vx / len;
                float vy2 = vy / len;
                int i3 = rotation;
                float f = x;
                float f2 = pitch3;
                float angle = (float) (Math.atan2((double) ((vx2 * -1.0f) - (vy2 * 0.0f)), (double) ((vx2 * 0.0f) + (vy2 * -1.0f))) / 0.017453292519943295d);
                if (angle < 0.0f) {
                    angle += 360.0f;
                }
                Callback callback2 = this.callback;
                if (callback2 != null) {
                    callback2.onOffsetsChanged(offsetX, offsetY, angle);
                    return;
                }
                return;
            }
        }
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}
