package com.google.android.gms.vision;

import android.util.SparseArray;
import com.google.android.gms.vision.Frame.Metadata;

public abstract class Detector<T> {
    private Object zzbMo = new Object();
    private Processor<T> zzbMp;

    public static class Detections<T> {
        private SparseArray<T> zzbMq;
        private Metadata zzbMr;
        private boolean zzbMs;

        public Detections(SparseArray<T> sparseArray, Metadata metadata, boolean z) {
            this.zzbMq = sparseArray;
            this.zzbMr = metadata;
            this.zzbMs = z;
        }

        public boolean detectorIsOperational() {
            return this.zzbMs;
        }

        public SparseArray<T> getDetectedItems() {
            return this.zzbMq;
        }

        public Metadata getFrameMetadata() {
            return this.zzbMr;
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
        synchronized (this.zzbMo) {
            if (this.zzbMp == null) {
                throw new IllegalStateException("Detector processor must first be set with setProcessor in order to receive detection results.");
            }
            Metadata metadata = new Metadata(frame.getMetadata());
            metadata.zzSm();
            this.zzbMp.receiveDetections(new Detections(detect(frame), metadata, isOperational()));
        }
    }

    public void release() {
        synchronized (this.zzbMo) {
            if (this.zzbMp != null) {
                this.zzbMp.release();
                this.zzbMp = null;
            }
        }
    }

    public boolean setFocus(int i) {
        return true;
    }

    public void setProcessor(Processor<T> processor) {
        this.zzbMp = processor;
    }
}
