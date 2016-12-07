package com.google.android.gms.vision;

import android.util.Log;
import android.util.SparseArray;
import com.google.android.gms.vision.Detector.Detections;
import com.google.android.gms.vision.Detector.Processor;

public abstract class FocusingProcessor<T> implements Processor<T> {
    private Detector<T> zzbMd;
    private Tracker<T> zzbMt;
    private int zzbMu = 3;
    private boolean zzbMv = false;
    private int zzbMw;
    private int zzbMx = 0;

    public FocusingProcessor(Detector<T> detector, Tracker<T> tracker) {
        this.zzbMd = detector;
        this.zzbMt = tracker;
    }

    public void receiveDetections(Detections<T> detections) {
        SparseArray detectedItems = detections.getDetectedItems();
        if (detectedItems.size() == 0) {
            if (this.zzbMx == this.zzbMu) {
                this.zzbMt.onDone();
                this.zzbMv = false;
            } else {
                this.zzbMt.onMissing(detections);
            }
            this.zzbMx++;
            return;
        }
        this.zzbMx = 0;
        if (this.zzbMv) {
            Object obj = detectedItems.get(this.zzbMw);
            if (obj != null) {
                this.zzbMt.onUpdate(detections, obj);
                return;
            } else {
                this.zzbMt.onDone();
                this.zzbMv = false;
            }
        }
        int selectFocus = selectFocus(detections);
        Object obj2 = detectedItems.get(selectFocus);
        if (obj2 == null) {
            Log.w("FocusingProcessor", "Invalid focus selected: " + selectFocus);
            return;
        }
        this.zzbMv = true;
        this.zzbMw = selectFocus;
        this.zzbMd.setFocus(this.zzbMw);
        this.zzbMt.onNewItem(this.zzbMw, obj2);
        this.zzbMt.onUpdate(detections, obj2);
    }

    public void release() {
        this.zzbMt.onDone();
    }

    public abstract int selectFocus(Detections<T> detections);

    protected void zznf(int i) {
        if (i < 0) {
            throw new IllegalArgumentException("Invalid max gap: " + i);
        }
        this.zzbMu = i;
    }
}
