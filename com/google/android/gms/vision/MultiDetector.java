package com.google.android.gms.vision;

import android.util.SparseArray;
import com.google.android.gms.vision.Detector.Processor;
import java.util.ArrayList;
import java.util.List;

public class MultiDetector extends Detector<Object> {
    private List<Detector<? extends Object>> zzbOz;

    public static class Builder {
        private MultiDetector zzbOA = new MultiDetector();

        public Builder add(Detector<? extends Object> detector) {
            this.zzbOA.zzbOz.add(detector);
            return this;
        }

        public MultiDetector build() {
            if (this.zzbOA.zzbOz.size() != 0) {
                return this.zzbOA;
            }
            throw new RuntimeException("No underlying detectors added to MultiDetector.");
        }
    }

    private MultiDetector() {
        this.zzbOz = new ArrayList();
    }

    public SparseArray<Object> detect(Frame frame) {
        SparseArray<Object> sparseArray = new SparseArray();
        for (Detector detect : this.zzbOz) {
            SparseArray detect2 = detect.detect(frame);
            for (int i = 0; i < detect2.size(); i++) {
                int keyAt = detect2.keyAt(i);
                if (sparseArray.get(keyAt) != null) {
                    throw new IllegalStateException("Detection ID overlap for id = " + keyAt + ".  This means that one of the detectors is not using global IDs.");
                }
                sparseArray.append(keyAt, detect2.valueAt(i));
            }
        }
        return sparseArray;
    }

    public boolean isOperational() {
        for (Detector isOperational : this.zzbOz) {
            if (!isOperational.isOperational()) {
                return false;
            }
        }
        return true;
    }

    public void receiveFrame(Frame frame) {
        for (Detector receiveFrame : this.zzbOz) {
            receiveFrame.receiveFrame(frame);
        }
    }

    public void release() {
        for (Detector release : this.zzbOz) {
            release.release();
        }
        this.zzbOz.clear();
    }

    public void setProcessor(Processor<Object> processor) {
        throw new UnsupportedOperationException("MultiDetector.setProcessor is not supported.  You should set a processor instance on each underlying detector instead.");
    }
}
