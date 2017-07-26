package com.google.android.gms.vision;

import android.util.Log;
import android.util.SparseArray;
import com.google.android.gms.vision.Detector.Detections;
import com.google.android.gms.vision.Detector.Processor;

public abstract class FocusingProcessor<T> implements Processor<T> {
    private Detector<T> zzbMB;
    private Tracker<T> zzbMQ;
    private int zzbMR = 3;
    private boolean zzbMS = false;
    private int zzbMT;
    private int zzbMU = 0;

    public FocusingProcessor(Detector<T> detector, Tracker<T> tracker) {
        this.zzbMB = detector;
        this.zzbMQ = tracker;
    }

    public void receiveDetections(Detections<T> detections) {
        SparseArray detectedItems = detections.getDetectedItems();
        if (detectedItems.size() == 0) {
            if (this.zzbMU == this.zzbMR) {
                this.zzbMQ.onDone();
                this.zzbMS = false;
            } else {
                this.zzbMQ.onMissing(detections);
            }
            this.zzbMU++;
            return;
        }
        this.zzbMU = 0;
        if (this.zzbMS) {
            Object obj = detectedItems.get(this.zzbMT);
            if (obj != null) {
                this.zzbMQ.onUpdate(detections, obj);
                return;
            } else {
                this.zzbMQ.onDone();
                this.zzbMS = false;
            }
        }
        int selectFocus = selectFocus(detections);
        Object obj2 = detectedItems.get(selectFocus);
        if (obj2 == null) {
            Log.w("FocusingProcessor", "Invalid focus selected: " + selectFocus);
            return;
        }
        this.zzbMS = true;
        this.zzbMT = selectFocus;
        this.zzbMB.setFocus(this.zzbMT);
        this.zzbMQ.onNewItem(this.zzbMT, obj2);
        this.zzbMQ.onUpdate(detections, obj2);
    }

    public void release() {
        this.zzbMQ.onDone();
    }

    public abstract int selectFocus(Detections<T> detections);

    protected final void zzbK(int i) {
        if (i < 0) {
            throw new IllegalArgumentException("Invalid max gap: " + i);
        }
        this.zzbMR = i;
    }
}
