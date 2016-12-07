package com.google.android.gms.vision;

import android.util.Log;
import android.util.SparseArray;
import com.google.android.gms.vision.Detector.Detections;
import com.google.android.gms.vision.Detector.Processor;

public abstract class FocusingProcessor<T> implements Processor<T> {
    private Tracker<T> aNG;
    private int aNH = 3;
    private boolean aNI = false;
    private int aNJ;
    private int aNK = 0;
    private Detector<T> aNq;

    public FocusingProcessor(Detector<T> detector, Tracker<T> tracker) {
        this.aNq = detector;
        this.aNG = tracker;
    }

    public void receiveDetections(Detections<T> detections) {
        SparseArray detectedItems = detections.getDetectedItems();
        if (detectedItems.size() == 0) {
            if (this.aNK == this.aNH) {
                this.aNG.onDone();
                this.aNI = false;
            } else {
                this.aNG.onMissing(detections);
            }
            this.aNK++;
            return;
        }
        this.aNK = 0;
        if (this.aNI) {
            Object obj = detectedItems.get(this.aNJ);
            if (obj != null) {
                this.aNG.onUpdate(detections, obj);
                return;
            } else {
                this.aNG.onDone();
                this.aNI = false;
            }
        }
        int selectFocus = selectFocus(detections);
        Object obj2 = detectedItems.get(selectFocus);
        if (obj2 == null) {
            Log.w("FocusingProcessor", "Invalid focus selected: " + selectFocus);
            return;
        }
        this.aNI = true;
        this.aNJ = selectFocus;
        this.aNq.setFocus(this.aNJ);
        this.aNG.onNewItem(this.aNJ, obj2);
        this.aNG.onUpdate(detections, obj2);
    }

    public void release() {
        this.aNG.onDone();
    }

    public abstract int selectFocus(Detections<T> detections);

    protected void zzaaq(int i) {
        if (i < 0) {
            throw new IllegalArgumentException("Invalid max gap: " + i);
        }
        this.aNH = i;
    }
}
