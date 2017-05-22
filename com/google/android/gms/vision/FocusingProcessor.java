package com.google.android.gms.vision;

import android.util.Log;
import android.util.SparseArray;
import com.google.android.gms.vision.Detector.Detections;
import com.google.android.gms.vision.Detector.Processor;

public abstract class FocusingProcessor<T> implements Processor<T> {
    private Detector<T> zzbOc;
    private Tracker<T> zzbOr;
    private int zzbOs = 3;
    private boolean zzbOt = false;
    private int zzbOu;
    private int zzbOv = 0;

    public FocusingProcessor(Detector<T> detector, Tracker<T> tracker) {
        this.zzbOc = detector;
        this.zzbOr = tracker;
    }

    public void receiveDetections(Detections<T> detections) {
        SparseArray detectedItems = detections.getDetectedItems();
        if (detectedItems.size() == 0) {
            if (this.zzbOv == this.zzbOs) {
                this.zzbOr.onDone();
                this.zzbOt = false;
            } else {
                this.zzbOr.onMissing(detections);
            }
            this.zzbOv++;
            return;
        }
        this.zzbOv = 0;
        if (this.zzbOt) {
            Object obj = detectedItems.get(this.zzbOu);
            if (obj != null) {
                this.zzbOr.onUpdate(detections, obj);
                return;
            } else {
                this.zzbOr.onDone();
                this.zzbOt = false;
            }
        }
        int selectFocus = selectFocus(detections);
        Object obj2 = detectedItems.get(selectFocus);
        if (obj2 == null) {
            Log.w("FocusingProcessor", "Invalid focus selected: " + selectFocus);
            return;
        }
        this.zzbOt = true;
        this.zzbOu = selectFocus;
        this.zzbOc.setFocus(this.zzbOu);
        this.zzbOr.onNewItem(this.zzbOu, obj2);
        this.zzbOr.onUpdate(detections, obj2);
    }

    public void release() {
        this.zzbOr.onDone();
    }

    public abstract int selectFocus(Detections<T> detections);

    protected void zznQ(int i) {
        if (i < 0) {
            throw new IllegalArgumentException("Invalid max gap: " + i);
        }
        this.zzbOs = i;
    }
}
