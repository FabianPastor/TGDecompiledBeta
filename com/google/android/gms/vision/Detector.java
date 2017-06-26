package com.google.android.gms.vision;

import android.util.SparseArray;
import com.google.android.gms.vision.Frame.Metadata;

public abstract class Detector<T> {
    private final Object zzbMJ = new Object();
    private Processor<T> zzbMK;

    public static class Detections<T> {
        private SparseArray<T> zzbML;
        private Metadata zzbMM;
        private boolean zzbMN;

        public Detections(SparseArray<T> sparseArray, Metadata metadata, boolean z) {
            this.zzbML = sparseArray;
            this.zzbMM = metadata;
            this.zzbMN = z;
        }

        public boolean detectorIsOperational() {
            return this.zzbMN;
        }

        public SparseArray<T> getDetectedItems() {
            return this.zzbML;
        }

        public Metadata getFrameMetadata() {
            return this.zzbMM;
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
        synchronized (this.zzbMJ) {
            if (this.zzbMK == null) {
                throw new IllegalStateException("Detector processor must first be set with setProcessor in order to receive detection results.");
            }
            Metadata metadata = new Metadata(frame.getMetadata());
            metadata.zzDM();
            this.zzbMK.receiveDetections(new Detections(detect(frame), metadata, isOperational()));
        }
    }

    public void release() {
        synchronized (this.zzbMJ) {
            if (this.zzbMK != null) {
                this.zzbMK.release();
                this.zzbMK = null;
            }
        }
    }

    public boolean setFocus(int i) {
        return true;
    }

    public void setProcessor(Processor<T> processor) {
        this.zzbMK = processor;
    }
}
