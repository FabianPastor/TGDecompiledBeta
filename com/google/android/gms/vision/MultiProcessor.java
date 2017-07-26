package com.google.android.gms.vision;

import android.util.SparseArray;
import com.google.android.gms.vision.Detector.Detections;
import com.google.android.gms.vision.Detector.Processor;
import java.util.HashSet;
import java.util.Set;

public class MultiProcessor<T> implements Processor<T> {
    private int zzbMR;
    private Factory<T> zzbNd;
    private SparseArray<zza> zzbNe;

    public static class Builder<T> {
        private MultiProcessor<T> zzbNf = new MultiProcessor();

        public Builder(Factory<T> factory) {
            if (factory == null) {
                throw new IllegalArgumentException("No factory supplied.");
            }
            this.zzbNf.zzbNd = factory;
        }

        public MultiProcessor<T> build() {
            return this.zzbNf;
        }

        public Builder<T> setMaxGapFrames(int i) {
            if (i < 0) {
                throw new IllegalArgumentException("Invalid max gap: " + i);
            }
            this.zzbNf.zzbMR = i;
            return this;
        }
    }

    public interface Factory<T> {
        Tracker<T> create(T t);
    }

    class zza {
        private Tracker<T> zzbMQ;
        private int zzbMU;

        private zza(MultiProcessor multiProcessor) {
            this.zzbMU = 0;
        }
    }

    private MultiProcessor() {
        this.zzbNe = new SparseArray();
        this.zzbMR = 3;
    }

    private final void zza(Detections<T> detections) {
        SparseArray detectedItems = detections.getDetectedItems();
        for (int i = 0; i < detectedItems.size(); i++) {
            int keyAt = detectedItems.keyAt(i);
            Object valueAt = detectedItems.valueAt(i);
            zza com_google_android_gms_vision_MultiProcessor_zza = (zza) this.zzbNe.get(keyAt);
            com_google_android_gms_vision_MultiProcessor_zza.zzbMU = 0;
            com_google_android_gms_vision_MultiProcessor_zza.zzbMQ.onUpdate(detections, valueAt);
        }
    }

    public void receiveDetections(Detections<T> detections) {
        int i = 0;
        SparseArray detectedItems = detections.getDetectedItems();
        for (int i2 = 0; i2 < detectedItems.size(); i2++) {
            int keyAt = detectedItems.keyAt(i2);
            Object valueAt = detectedItems.valueAt(i2);
            if (this.zzbNe.get(keyAt) == null) {
                zza com_google_android_gms_vision_MultiProcessor_zza = new zza();
                com_google_android_gms_vision_MultiProcessor_zza.zzbMQ = this.zzbNd.create(valueAt);
                com_google_android_gms_vision_MultiProcessor_zza.zzbMQ.onNewItem(keyAt, valueAt);
                this.zzbNe.append(keyAt, com_google_android_gms_vision_MultiProcessor_zza);
            }
        }
        detectedItems = detections.getDetectedItems();
        Set<Integer> hashSet = new HashSet();
        while (i < this.zzbNe.size()) {
            int keyAt2 = this.zzbNe.keyAt(i);
            if (detectedItems.get(keyAt2) == null) {
                zza com_google_android_gms_vision_MultiProcessor_zza2 = (zza) this.zzbNe.valueAt(i);
                com_google_android_gms_vision_MultiProcessor_zza2.zzbMU = com_google_android_gms_vision_MultiProcessor_zza2.zzbMU + 1;
                if (com_google_android_gms_vision_MultiProcessor_zza2.zzbMU >= this.zzbMR) {
                    com_google_android_gms_vision_MultiProcessor_zza2.zzbMQ.onDone();
                    hashSet.add(Integer.valueOf(keyAt2));
                } else {
                    com_google_android_gms_vision_MultiProcessor_zza2.zzbMQ.onMissing(detections);
                }
            }
            i++;
        }
        for (Integer intValue : hashSet) {
            this.zzbNe.delete(intValue.intValue());
        }
        zza(detections);
    }

    public void release() {
        for (int i = 0; i < this.zzbNe.size(); i++) {
            ((zza) this.zzbNe.valueAt(i)).zzbMQ.onDone();
        }
        this.zzbNe.clear();
    }
}
