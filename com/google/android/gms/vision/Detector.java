package com.google.android.gms.vision;

import android.util.SparseArray;
import com.google.android.gms.vision.Frame.Metadata;

public abstract class Detector<T> {
    private Object aNB = new Object();
    private Processor<T> aNC;

    public static class Detections<T> {
        private SparseArray<T> aND;
        private Metadata aNE;
        private boolean aNF;

        public Detections(SparseArray<T> sparseArray, Metadata metadata, boolean z) {
            this.aND = sparseArray;
            this.aNE = metadata;
            this.aNF = z;
        }

        public boolean detectorIsOperational() {
            return this.aNF;
        }

        public SparseArray<T> getDetectedItems() {
            return this.aND;
        }

        public Metadata getFrameMetadata() {
            return this.aNE;
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
        synchronized (this.aNB) {
            if (this.aNC == null) {
                throw new IllegalStateException("Detector processor must first be set with setProcessor in order to receive detection results.");
            }
            Metadata metadata = new Metadata(frame.getMetadata());
            metadata.zzclo();
            this.aNC.receiveDetections(new Detections(detect(frame), metadata, isOperational()));
        }
    }

    public void release() {
        synchronized (this.aNB) {
            if (this.aNC != null) {
                this.aNC.release();
                this.aNC = null;
            }
        }
    }

    public boolean setFocus(int i) {
        return true;
    }

    public void setProcessor(Processor<T> processor) {
        this.aNC = processor;
    }
}
