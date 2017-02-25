package com.google.android.gms.vision;

import android.util.Log;
import android.util.SparseArray;
import com.google.android.gms.vision.Detector.Detections;
import com.google.android.gms.vision.Detector.Processor;

public abstract class FocusingProcessor<T> implements Processor<T> {
    private Detector<T> zzbOd;
    private Tracker<T> zzbOs;
    private int zzbOt = 3;
    private boolean zzbOu = false;
    private int zzbOv;
    private int zzbOw = 0;

    public FocusingProcessor(Detector<T> detector, Tracker<T> tracker) {
        this.zzbOd = detector;
        this.zzbOs = tracker;
    }

    public void receiveDetections(Detections<T> detections) {
        SparseArray detectedItems = detections.getDetectedItems();
        if (detectedItems.size() == 0) {
            if (this.zzbOw == this.zzbOt) {
                this.zzbOs.onDone();
                this.zzbOu = false;
            } else {
                this.zzbOs.onMissing(detections);
            }
            this.zzbOw++;
            return;
        }
        this.zzbOw = 0;
        if (this.zzbOu) {
            Object obj = detectedItems.get(this.zzbOv);
            if (obj != null) {
                this.zzbOs.onUpdate(detections, obj);
                return;
            } else {
                this.zzbOs.onDone();
                this.zzbOu = false;
            }
        }
        int selectFocus = selectFocus(detections);
        Object obj2 = detectedItems.get(selectFocus);
        if (obj2 == null) {
            Log.w("FocusingProcessor", "Invalid focus selected: " + selectFocus);
            return;
        }
        this.zzbOu = true;
        this.zzbOv = selectFocus;
        this.zzbOd.setFocus(this.zzbOv);
        this.zzbOs.onNewItem(this.zzbOv, obj2);
        this.zzbOs.onUpdate(detections, obj2);
    }

    public void release() {
        this.zzbOs.onDone();
    }

    public abstract int selectFocus(Detections<T> detections);

    protected void zznQ(int i) {
        if (i < 0) {
            throw new IllegalArgumentException("Invalid max gap: " + i);
        }
        this.zzbOt = i;
    }
}
