package com.google.android.gms.vision;

import android.util.SparseArray;
import com.google.android.gms.vision.Frame.Metadata;

public abstract class Detector<T> {
    private Object aKq = new Object();
    private Processor<T> aKr;

    public static class Detections<T> {
        private SparseArray<T> aKs;
        private Metadata aKt;
        private boolean aKu;

        public Detections(SparseArray<T> sparseArray, Metadata metadata, boolean z) {
            this.aKs = sparseArray;
            this.aKt = metadata;
            this.aKu = z;
        }

        public boolean detectorIsOperational() {
            return this.aKu;
        }

        public SparseArray<T> getDetectedItems() {
            return this.aKs;
        }

        public Metadata getFrameMetadata() {
            return this.aKt;
        }
    }

    public interface Processor<T> {
        void receiveDetections(Detections<T> detections);

        void release();
    }

    public abstract SparseArray<T> detect(Frame frame);

    public boolean isOperational() {
        return true;
    }

    public void receiveFrame(Frame frame) {
        synchronized (this.aKq) {
            if (this.aKr == null) {
                throw new IllegalStateException("Detector processor must first be set with setProcessor in order to receive detection results.");
            }
            Metadata metadata = new Metadata(frame.getMetadata());
            metadata.zzclp();
            this.aKr.receiveDetections(new Detections(detect(frame), metadata, isOperational()));
        }
    }

    public void release() {
        synchronized (this.aKq) {
            if (this.aKr != null) {
                this.aKr.release();
                this.aKr = null;
            }
        }
    }

    public boolean setFocus(int i) {
        return true;
    }

    public void setProcessor(Processor<T> processor) {
        this.aKr = processor;
    }
}
