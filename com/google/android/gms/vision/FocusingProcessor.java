package com.google.android.gms.vision;

import android.util.Log;
import android.util.SparseArray;
import com.google.android.gms.vision.Detector.Detections;
import com.google.android.gms.vision.Detector.Processor;

public abstract class FocusingProcessor<T> implements Processor<T> {
    private Detector<T> zzbNZ;
    private Tracker<T> zzbOo;
    private int zzbOp = 3;
    private boolean zzbOq = false;
    private int zzbOr;
    private int zzbOs = 0;

    public FocusingProcessor(Detector<T> detector, Tracker<T> tracker) {
        this.zzbNZ = detector;
        this.zzbOo = tracker;
    }

    public void receiveDetections(Detections<T> detections) {
        SparseArray detectedItems = detections.getDetectedItems();
        if (detectedItems.size() == 0) {
            if (this.zzbOs == this.zzbOp) {
                this.zzbOo.onDone();
                this.zzbOq = false;
            } else {
                this.zzbOo.onMissing(detections);
            }
            this.zzbOs++;
            return;
        }
        this.zzbOs = 0;
        if (this.zzbOq) {
            Object obj = detectedItems.get(this.zzbOr);
            if (obj != null) {
                this.zzbOo.onUpdate(detections, obj);
                return;
            } else {
                this.zzbOo.onDone();
                this.zzbOq = false;
            }
        }
        int selectFocus = selectFocus(detections);
        Object obj2 = detectedItems.get(selectFocus);
        if (obj2 == null) {
            Log.w("FocusingProcessor", "Invalid focus selected: " + selectFocus);
            return;
        }
        this.zzbOq = true;
        this.zzbOr = selectFocus;
        this.zzbNZ.setFocus(this.zzbOr);
        this.zzbOo.onNewItem(this.zzbOr, obj2);
        this.zzbOo.onUpdate(detections, obj2);
    }

    public void release() {
        this.zzbOo.onDone();
    }

    public abstract int selectFocus(Detections<T> detections);

    protected void zznQ(int i) {
        if (i < 0) {
            throw new IllegalArgumentException("Invalid max gap: " + i);
        }
        this.zzbOp = i;
    }
}
