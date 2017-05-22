package com.google.android.gms.vision;

import android.util.SparseArray;
import com.google.android.gms.vision.Frame.Metadata;

public abstract class Detector<T> {
    private Object zzbOm = new Object();
    private Processor<T> zzbOn;

    public static class Detections<T> {
        private SparseArray<T> zzbOo;
        private Metadata zzbOp;
        private boolean zzbOq;

        public Detections(SparseArray<T> sparseArray, Metadata metadata, boolean z) {
            this.zzbOo = sparseArray;
            this.zzbOp = metadata;
            this.zzbOq = z;
        }

        public boolean detectorIsOperational() {
            return this.zzbOq;
        }

        public SparseArray<T> getDetectedItems() {
            return this.zzbOo;
        }

        public Metadata getFrameMetadata() {
            return this.zzbOp;
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
        synchronized (this.zzbOm) {
            if (this.zzbOn == null) {
                throw new IllegalStateException("Detector processor must first be set with setProcessor in order to receive detection results.");
            }
            Metadata metadata = new Metadata(frame.getMetadata());
            metadata.zzTQ();
            this.zzbOn.receiveDetections(new Detections(detect(frame), metadata, isOperational()));
        }
    }

    public void release() {
        synchronized (this.zzbOm) {
            if (this.zzbOn != null) {
                this.zzbOn.release();
                this.zzbOn = null;
            }
        }
    }

    public boolean setFocus(int i) {
        return true;
    }

    public void setProcessor(Processor<T> processor) {
        this.zzbOn = processor;
    }
}
