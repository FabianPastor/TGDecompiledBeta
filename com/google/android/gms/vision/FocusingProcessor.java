package com.google.android.gms.vision;

import android.util.Log;
import android.util.SparseArray;
import com.google.android.gms.vision.Detector.Detections;
import com.google.android.gms.vision.Detector.Processor;

public abstract class FocusingProcessor<T> implements Processor<T> {
    private Tracker<T> zzbMO;
    private int zzbMP = 3;
    private boolean zzbMQ = false;
    private int zzbMR;
    private int zzbMS = 0;
    private Detector<T> zzbMz;

    public FocusingProcessor(Detector<T> detector, Tracker<T> tracker) {
        this.zzbMz = detector;
        this.zzbMO = tracker;
    }

    public void receiveDetections(Detections<T> detections) {
        SparseArray detectedItems = detections.getDetectedItems();
        if (detectedItems.size() == 0) {
            if (this.zzbMS == this.zzbMP) {
                this.zzbMO.onDone();
                this.zzbMQ = false;
            } else {
                this.zzbMO.onMissing(detections);
            }
            this.zzbMS++;
            return;
        }
        this.zzbMS = 0;
        if (this.zzbMQ) {
            Object obj = detectedItems.get(this.zzbMR);
            if (obj != null) {
                this.zzbMO.onUpdate(detections, obj);
                return;
            } else {
                this.zzbMO.onDone();
                this.zzbMQ = false;
            }
        }
        int selectFocus = selectFocus(detections);
        Object obj2 = detectedItems.get(selectFocus);
        if (obj2 == null) {
            Log.w("FocusingProcessor", "Invalid focus selected: " + selectFocus);
            return;
        }
        this.zzbMQ = true;
        this.zzbMR = selectFocus;
        this.zzbMz.setFocus(this.zzbMR);
        this.zzbMO.onNewItem(this.zzbMR, obj2);
        this.zzbMO.onUpdate(detections, obj2);
    }

    public void release() {
        this.zzbMO.onDone();
    }

    public abstract int selectFocus(Detections<T> detections);

    protected final void zzbK(int i) {
        if (i < 0) {
            throw new IllegalArgumentException("Invalid max gap: " + i);
        }
        this.zzbMP = i;
    }
}
