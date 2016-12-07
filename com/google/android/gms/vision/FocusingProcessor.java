package com.google.android.gms.vision;

import android.util.Log;
import android.util.SparseArray;
import com.google.android.gms.vision.Detector.Detections;
import com.google.android.gms.vision.Detector.Processor;

public abstract class FocusingProcessor<T> implements Processor<T> {
    private Detector<T> aKf;
    private Tracker<T> aKv;
    private int aKw = 3;
    private boolean aKx = false;
    private int aKy;
    private int aKz = 0;

    public FocusingProcessor(Detector<T> detector, Tracker<T> tracker) {
        this.aKf = detector;
        this.aKv = tracker;
    }

    public void receiveDetections(Detections<T> detections) {
        SparseArray detectedItems = detections.getDetectedItems();
        if (detectedItems.size() == 0) {
            if (this.aKz == this.aKw) {
                this.aKv.onDone();
                this.aKx = false;
            } else {
                this.aKv.onMissing(detections);
            }
            this.aKz++;
            return;
        }
        this.aKz = 0;
        if (this.aKx) {
            Object obj = detectedItems.get(this.aKy);
            if (obj != null) {
                this.aKv.onUpdate(detections, obj);
                return;
            } else {
                this.aKv.onDone();
                this.aKx = false;
            }
        }
        int selectFocus = selectFocus(detections);
        Object obj2 = detectedItems.get(selectFocus);
        if (obj2 == null) {
            Log.w("FocusingProcessor", "Invalid focus selected: " + selectFocus);
            return;
        }
        this.aKx = true;
        this.aKy = selectFocus;
        this.aKf.setFocus(this.aKy);
        this.aKv.onNewItem(this.aKy, obj2);
        this.aKv.onUpdate(detections, obj2);
    }

    public void release() {
        this.aKv.onDone();
    }

    public abstract int selectFocus(Detections<T> detections);

    protected void zzaba(int i) {
        if (i < 0) {
            throw new IllegalArgumentException("Invalid max gap: " + i);
        }
        this.aKw = i;
    }
}
