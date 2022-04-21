package org.webrtc;

import android.view.OrientationEventListener;
import org.telegram.messenger.ApplicationLoader;

public class OrientationHelper {
    private static final int ORIENTATION_HYSTERESIS = 5;
    public static volatile int cameraOrientation;
    public static volatile int cameraRotation;
    /* access modifiers changed from: private */
    public OrientationEventListener orientationEventListener = new OrientationEventListener(ApplicationLoader.applicationContext) {
        public void onOrientationChanged(int orientation) {
            if (OrientationHelper.this.orientationEventListener != null && orientation != -1) {
                OrientationHelper orientationHelper = OrientationHelper.this;
                int newOrietation = orientationHelper.roundOrientation(orientation, orientationHelper.rotation);
                if (newOrietation != OrientationHelper.this.rotation) {
                    OrientationHelper orientationHelper2 = OrientationHelper.this;
                    orientationHelper2.onOrientationUpdate(orientationHelper2.rotation = newOrietation);
                }
            }
        }
    };
    /* access modifiers changed from: private */
    public int rotation;

    /* access modifiers changed from: private */
    public int roundOrientation(int orientation, int orientationHistory) {
        int dist;
        if (orientationHistory == -1) {
            dist = 1;
        } else {
            int dist2 = Math.abs(orientation - orientationHistory);
            dist = Math.min(dist2, 360 - dist2) >= 50 ? 1 : 0;
        }
        if (dist != 0) {
            return (((orientation + 45) / 90) * 90) % 360;
        }
        return orientationHistory;
    }

    /* access modifiers changed from: protected */
    public void onOrientationUpdate(int orientation) {
    }

    public void start() {
        if (this.orientationEventListener.canDetectOrientation()) {
            this.orientationEventListener.enable();
            return;
        }
        this.orientationEventListener.disable();
        this.orientationEventListener = null;
    }

    public void stop() {
        OrientationEventListener orientationEventListener2 = this.orientationEventListener;
        if (orientationEventListener2 != null) {
            orientationEventListener2.disable();
            this.orientationEventListener = null;
        }
    }

    public int getOrientation() {
        return this.rotation;
    }
}
