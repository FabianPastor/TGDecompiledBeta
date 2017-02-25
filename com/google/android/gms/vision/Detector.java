package com.google.android.gms.vision;

import android.util.SparseArray;
import com.google.android.gms.vision.Frame.Metadata;

public abstract class Detector<T> {
    private Object zzbOn = new Object();
    private Processor<T> zzbOo;

    public static class Detections<T> {
        private SparseArray<T> zzbOp;
        private Metadata zzbOq;
        private boolean zzbOr;

        public Detections(SparseArray<T> sparseArray, Metadata metadata, boolean z) {
            this.zzbOp = sparseArray;
            this.zzbOq = metadata;
            this.zzbOr = z;
        }

        public boolean detectorIsOperational() {
            return this.zzbOr;
        }

        public SparseArray<T> getDetectedItems() {
            return this.zzbOp;
        }

        public Metadata getFrameMetadata() {
            return this.zzbOq;
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
        synchronized (this.zzbOn) {
            if (this.zzbOo == null) {
                throw new IllegalStateException("Detector processor must first be set with setProcessor in order to receive detection results.");
            }
            Metadata metadata = new Metadata(frame.getMetadata());
            metadata.zzTN();
            this.zzbOo.receiveDetections(new Detections(detect(frame), metadata, isOperational()));
        }
    }

    public void release() {
        synchronized (this.zzbOn) {
            if (this.zzbOo != null) {
                this.zzbOo.release();
                this.zzbOo = null;
            }
        }
    }

    public boolean setFocus(int i) {
        return true;
    }

    public void setProcessor(Processor<T> processor) {
        this.zzbOo = processor;
    }
}
